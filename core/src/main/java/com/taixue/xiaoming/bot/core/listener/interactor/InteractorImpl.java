package com.taixue.xiaoming.bot.core.listener.interactor;

import com.taixue.xiaoming.bot.api.annotation.InteractMethod;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorMethodDetail;
import com.taixue.xiaoming.bot.api.exception.InteactorTimeoutException;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.GroupInteractorUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.InteractorUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.MessageWaiter;
import com.taixue.xiaoming.bot.api.listener.interactor.user.PrivateInteractorUser;
import com.taixue.xiaoming.bot.core.base.PluginObjectImpl;
import com.taixue.xiaoming.bot.core.listener.interactor.user.GroupInteractorUserImpl;
import com.taixue.xiaoming.bot.core.listener.interactor.user.MessageWaiterImpl;
import com.taixue.xiaoming.bot.core.listener.interactor.user.PrivateInteractorUserImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;
import com.taixue.xiaoming.bot.util.NoParameterMethod;
import kotlinx.coroutines.TimeoutCancellationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class InteractorImpl
        extends PluginObjectImpl implements Interactor {
    private final Map<Long, InteractorUser> islocater = new HashMap<>();

    private final Set<InteractorMethodDetail> interactorMethodDetails = new HashSet<>();

    /**
     * 重载交互方法详情
     */
    @Override
    public void reloadInteractorDetails() {
        interactorMethodDetails.clear();
        final Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(InteractMethod.class)) {
                continue;
            }
            InteractorMethodDetail detail = new InteractorMethodDetailImpl(method);
            interactorMethodDetails.add(detail);
        }
    }

    @Override
    public Set<InteractorMethodDetail> getInteractorMethodDetails() {
        return interactorMethodDetails;
    }

    /**
     * 设置本次退出后离开本交互器
     * @param qq
     */
    @Override
    public final void setWillExit(final long qq) {
        setWillExit(getUser(qq));
    }

    @Override
    public final void setWillExit(@NotNull final InteractorUser user) {
        user.shouldExit();
    }

    @Override
    public final boolean isWillExit(long qq) {
        final InteractorUser user = getUser(qq);
        return Objects.nonNull(user) && user.isExit();
    }

    @Override
    public final boolean isWillExit(@NotNull final InteractorUser user) {
        return isWillExit(user.getQQ());
    }

    /**
     * 用户初次使用交互器的方法（自动注册）
     * @param user
     */
    @Override
    public final void onUserIn(@NotNull final DispatcherUser user) {
        if (user instanceof GroupDispatcherUser) {
            onGroupUserIn(((GroupDispatcherUser) user));
        } else if (user instanceof PrivateDispatcherUser) {
            onPrivateUserIn(((PrivateDispatcherUser) user));
        } else {
            throw new IllegalArgumentException("dispatcher user must be instance of GroupDispatcherUser or PrivateDispatcherUser");
        }
    }

    /**
     * 群聊交互用户初次与本交互器交互时的操作
     * @param user 当前的群聊交互成员
     */
    @Override
    public final void onGroupUserIn(@NotNull final GroupDispatcherUser user) {
        if (!islocater.containsKey(user.getQQ())) {
            final GroupInteractorUser interactorUser = new GroupInteractorUserImpl();
            interactorUser.setGroupMsg(user.getGroupMsg());
            interactorUser.setMsgSender(user.getMsgSender());
            islocater.put(user.getQQ(), interactorUser);
            onGroupUserIn(interactorUser);
        } else {
            throw new IllegalArgumentException("group dispatcher user " + user.getQQString() + " already be initialized!");
        }
    }

    /**
     * 群聊交互用户在注册好交互器数据后的操作（用于重写的方法）
     * @param user 当前的群聊交互成员
     */
    @Override
    public void onGroupUserIn(@NotNull final GroupInteractorUser user) {
    }

    /**
     * 私聊交互用户初次与本交互器交互时的操作
     * @param user 当前的私聊交互成员
     */
    @Override
    public final void onPrivateUserIn(@NotNull final PrivateDispatcherUser user) {
        if (!islocater.containsKey(user.getQQ())) {
            final PrivateInteractorUser interactorUser = new PrivateInteractorUserImpl();
            interactorUser.setPrivateMsg(user.getPrivateMsg());
            interactorUser.setMsgSender(user.getMsgSender());
            islocater.put(user.getQQ(), interactorUser);
            onPrivateUserIn(interactorUser);
        } else {
            throw new IllegalArgumentException("private dispatcher user " + user.getQQString() + " already be initialized!");
        }
    }

    /**
     * 私聊交互用户在注册好交互器数据后的操作（用于重写的方法）
     * @param user 当前的群聊交互成员
     */
    @Override
    public void onPrivateUserIn(@NotNull final PrivateInteractorUser user) {
    }

    @Override
    public final void onUserExit(@NotNull final InteractorUser user) {
        removeUser(user.getQQ());
    }

    /**
     * 供调度器调度的交互接口
     * @param user 当前的调度器使用者
     * @return 是否成功交互
     * @throws Exception 交互出现的异常
     */
    @Override
    public final boolean interact(@NotNull final DispatcherUser user) throws Exception {
        // 先判断是否交互过，如果没有由交互器完成初始化操作
        InteractorUser interactorUser = getUser(user.getQQ());

        if (Objects.nonNull(interactorUser)) {
            if (interactorUser instanceof GroupInteractorUser != user instanceof GroupDispatcherUser) {
                // 类型不一致时意味着用户在群聊和私聊间穿越，此时转换用户数据，以当前交互场所为准，也就是 user 的类型
                final MessageWaiter messageWaiter = interactorUser.getMessageWaiter();
                if (user instanceof GroupDispatcherUser) {
                    // 从私聊穿越到群聊
                    interactorUser = new GroupInteractorUserImpl();
                    ((GroupInteractorUser) interactorUser).setGroupMsg(((GroupDispatcherUser) user).getGroupMsg());
                    putUser(user.getQQ(), interactorUser);
                } else if (user instanceof PrivateDispatcherUser) {
                    // 从群聊穿越到私聊
                    interactorUser = new PrivateInteractorUserImpl();
                    ((PrivateInteractorUser) interactorUser).setPrivateMsg(((PrivateDispatcherUser) user).getPrivateMsg());
                    putUser(user.getQQ(), interactorUser);
                }
                interactorUser.setMessageWaiter(messageWaiter);
                interactorUser.setMsgSender(user.getMsgSender());
                // interactorUser.setAccountInfo(user.getAccountInfo());
                // TODO: 2021/4/28 Check if it cause exception
            } else {
                // 类型一致时只需要设置本次的数据就可以调度去了
                if (interactorUser instanceof GroupInteractorUser) {
                    ((GroupInteractorUser) interactorUser).setGroupMsg(((GroupDispatcherUser) user).getGroupMsg());
                } else if (interactorUser instanceof PrivateInteractorUser) {
                    ((PrivateInteractorUser) interactorUser).setPrivateMsg(((PrivateDispatcherUser) user).getPrivateMsg());
                } else {
                    throw new IllegalArgumentException("interactor user must be instance of GroupInteractorUser or PrivateInteractorUser");
                }
            }
            return interact(interactorUser);
        } else {
            onUserIn(user);
            interactorUser = getUser(user.getQQ());
            return true;
        }
    }

    @Override
    public final boolean interact(@NotNull final InteractorUser user) throws Exception {
        if (user instanceof GroupInteractorUser || user instanceof PrivateInteractorUser) {
            final MessageWaiter messageWaiter = user.getMessageWaiter();
            if (Objects.nonNull(messageWaiter)) {
                onGetNextInput(user);
                return true;
            } else if (user.isFirstInteract()) {
                user.setFirstInteract(false);
                for (InteractorMethodDetail detail : interactorMethodDetails) {
                    final Method method = detail.getMethod();
                    // 检查有无权限
                    if (!user.hasPermissions(detail.getRequiredPermissions())) {
                        continue;
                    }

                    // 填充参数
                    final List<Object> arguments = new ArrayList<>();
                    final Parameter[] parameters = method.getParameters();

                    for (Parameter parameter : parameters) {
                        final Class<?> type = parameter.getType();

                        if (GroupInteractorUser.class.isAssignableFrom(type)) {
                            if (user instanceof GroupInteractorUser) {
                                arguments.add(user);
                            } else {
                                break;
                            }
                        } else if (PrivateInteractorUser.class.isAssignableFrom(type)) {
                            if (user instanceof PrivateInteractorUser) {
                                arguments.add(user);
                            } else {
                                break;
                            }
                        } else if (InteractorUser.class.isAssignableFrom(type)) {
                            if (user instanceof InteractorUser) {
                                arguments.add(user);
                            } else {
                                break;
                            }
                        } else {
                            throw new NoSuchElementException("parameters of interact method must all be instance of InteractorUser");
                        }
                    }

                    // 参数不一致说明填充失败，继续寻找方法
                    if (arguments.size() != parameters.length) {
                        continue;
                    }

                    try {
                        method.invoke(this, arguments.toArray(new Object[0]));
                        setWillExit(user);
                    } catch (TimeoutCancellationException ignored) {
                    } catch (InteactorTimeoutException exception) {
                        setWillExit(user);
                    }
                }
                return true;
            } else {
                throw new IllegalStateException("user " + user.getQQString() + " should exit");
            }
        } else {
            throw new IllegalArgumentException("interactor user must be instance of GroupInteractorUser or PrivateInteractorUser");
        }
    }

    @Override
    @NotNull
    public final NoParameterMethod getDefaultTimeoutMethod(@NotNull final InteractorUser user,
                                                           long timeOutTime) {
        return () -> {
            user.sendMessage("你已经{}没有理小明了，我们下次见哦", TimeUtil.toTimeString(timeOutTime));
            setWillExit(user);
            throw new InteactorTimeoutException(this);
        };
    }

    @Override
    @Nullable
    public final String getNextInput(@NotNull final InteractorUser user,
                                     long timeOutTime,
                                     @Nullable String defaultValue,
                                     @NotNull NoParameterMethod method) {
        MessageWaiter messageWaiter = new MessageWaiterImpl(System.currentTimeMillis() + timeOutTime, defaultValue);
        user.setMessageWaiter(messageWaiter);
        try {
            synchronized (messageWaiter) {
                messageWaiter.wait(timeOutTime);
            }
        } catch (InterruptedException e) {
        }

        String value = messageWaiter.getValue();
        if (Objects.isNull(value)) {
            method.execute();
        }
        user.setMessageWaiter(null);
        return value;
    }

    @Override
    public final void onGetNextInput(@NotNull final InteractorUser user) {
        user.getMessageWaiter().onInput(user.getMessage());
    }

    @Override
    public InteractorUser getUser(Long qq) {
        return islocater.get(qq);
    }

    @Override
    public void putUser(Long aLong, InteractorUser user) {
        islocater.put(aLong, user);
    }

    @Override
    public void removeUser(Long aLong) {
        islocater.remove(aLong);
    }
}