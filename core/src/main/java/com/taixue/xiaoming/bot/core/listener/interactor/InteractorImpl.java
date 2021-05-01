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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
public abstract class InteractorImpl extends PluginObjectImpl implements Interactor {
    /**
     * 用户数据隔离器
     */
    private final Map<Long, InteractorUser> islocater = new HashMap<>();

    /**
     * 交互方法记录器
     */
    private final Set<InteractorMethodDetail> interactorMethodDetails = new HashSet<>();

    /**
     * 重载交互方法详情
     */
    @Override
    public void reloadInteractorDetails() {
        interactorMethodDetails.clear();
        final Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotationsByType(InteractMethod.class).length != 0) {
                interactorMethodDetails.add(new InteractorMethodDetailImpl(method));
            }
        }
        if (interactorMethodDetails.isEmpty()) {
            getLogger().warn("没有从加载任何子交互方法");
        } else {
            getLogger().info("成功加载了 {} 个子交互方法", interactorMethodDetails.size());
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
        final InteractorUser user = getUser(qq);
        setWillExit(user);
    }

    @Override
    public final boolean isWillExit(long qq) {
        final InteractorUser user = getUser(qq);
        return Objects.isNull(user) || user.isExit();
    }

    @Override
    public final void onUserExit(final InteractorUser user) {
        removeUser(user.getQQ());
    }

    /**
     * 供调度器调度的交互接口
     * @param user 当前的调度器使用者
     * @return 是否成功交互
     * @throws Exception 交互出现的异常
     */
    @Override
    public final boolean interact(final DispatcherUser user) throws Exception {
        InteractorUser interactorUser = getUser(user.getQQ());

        if (Objects.nonNull(interactorUser)) {
            // 如果交互过，只需要设置数据来交互就行
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
                } else {
                    throw new IllegalArgumentException("interactor user must be instance of GroupInteractorUser or PrivateInteractorUser");
                }
                interactorUser.setMessageWaiter(messageWaiter);
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
            // 没交互过，设置相关信息后开始交互
            if (user instanceof GroupDispatcherUser) {
                interactorUser = new GroupInteractorUserImpl();
                ((GroupInteractorUserImpl) interactorUser).setGroupMsg(((GroupDispatcherUser) user).getGroupMsg());
            } else if (user instanceof PrivateDispatcherUser) {
                interactorUser = new PrivateInteractorUserImpl();
                ((PrivateInteractorUserImpl) interactorUser).setPrivateMsg(((PrivateDispatcherUser) user).getPrivateMsg());
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of GroupDispatcherUser or PrivateDispatcherUser");
            }
            putUser(user.getQQ(), interactorUser);
            return interact(interactorUser);
        }
    }

    public final boolean interact(final InteractorUser user) throws Exception {
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
                            final Object o = onParameter(user, parameter);
                            if (Objects.nonNull(o)) {
                                arguments.add(o);
                            } else {
                                throw new NoSuchElementException("parameters of interact method must all be instance of InteractorUser");
                            }
                        }
                    }

                    // 参数不一致说明填充失败，继续寻找方法
                    if (arguments.size() != parameters.length) {
                        continue;
                    }

                    try {
                        method.invoke(this, arguments.toArray(new Object[0]));
                        setWillExit(user);
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
    public final NoParameterMethod getDefaultTimeoutMethod(final InteractorUser user,
                                                           long timeOutTime) {
        return () -> {
            user.sendMessage("你已经{}没有理小明了，我们下次见哦", TimeUtil.toTimeString(timeOutTime));
            setWillExit(user);
            throw new InteactorTimeoutException(this);
        };
    }

    @Override
    @Nullable
    public final String getNextInput(final InteractorUser user,
                                     long timeOutTime,
                                     @Nullable String defaultValue,
                                     NoParameterMethod method) {
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
    public Object onParameter(final InteractorUser user, final Parameter parameter) {
        return null;
    }

    @Override
    public final void onGetNextInput(final InteractorUser user) {
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