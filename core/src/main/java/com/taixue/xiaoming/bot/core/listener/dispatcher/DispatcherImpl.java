package com.taixue.xiaoming.bot.core.listener.dispatcher;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountEvent;
import com.taixue.xiaoming.bot.api.error.ErrorMessageManager;
import com.taixue.xiaoming.bot.api.listener.dispatcher.Dispatcher;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.PrivateXiaomingUser;
import com.taixue.xiaoming.bot.core.account.AccountEventImpl;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.error.ErrorMessageImpl;
import kotlinx.coroutines.TimeoutCancellationException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Chuanwise
 */
public class DispatcherImpl<UserDataType extends DispatcherUser> extends HostObjectImpl implements Dispatcher<UserDataType> {
    final ErrorMessageManager errorMessageManager = getXiaomingBot().getErrorMessageManager();

    @Override
    public boolean onMessage(UserDataType user) {
        final String message = user.getMessage();
        final Account account = getXiaomingBot().getAccountManager().getAccount(user.getQQ());

        // 寻找指令处理器
        CommandExecutor finalCommandExecutor = null;
        try {
            final CommandManager commandManager = getXiaomingBot().getCommandManager();
            for (CommandExecutor executor : commandManager.getCoreCommandExecutors()) {
                finalCommandExecutor = executor;
                if (executor.onCommand(user)) {
                    // 记录日志
                    final String eventMessage = "执行指令：" + user.getMessage();
                    getLogger().info(user.getCompleteName() + " " + eventMessage);
                    if (user instanceof GroupXiaomingUser) {
                        user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
                    } else {
                        user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                    }
                    user.getOrPutAccount().readySave();
                    return true;
                }
            }
            if (user instanceof GroupXiaomingUser) {
                final Group group = getXiaomingBot().getGroupManager().forGroup(((GroupXiaomingUser) user).getGroup());
                for (Map.Entry<XiaomingPlugin, Set<CommandExecutor>> entry : commandManager.getPluginCommandExecutors().entrySet()) {
                    final XiaomingPlugin key = entry.getKey();
                    // 如果该用户没有禁用该插件，且本群也没禁用
                    if (!group.isUnablePlugin(key.getName()) &&
                            (Objects.isNull(account) || !account.isBlockPlugin(key.getName()))) {
                        for (CommandExecutor executor : entry.getValue()) {
                            finalCommandExecutor = executor;
                            if (executor.onCommand(user)) {
                                // 记录日志
                                final String eventMessage = "执行插件 " + key.getCompleteName() + " 的指令：" + user.getMessage();
                                getLogger().info(user.getCompleteName() + " " + eventMessage);
                                user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
                                user.getOrPutAccount().readySave();
                                return true;
                            }
                        }
                    }
                }
            } else if (user instanceof PrivateXiaomingUser) {
                for (Map.Entry<XiaomingPlugin, Set<CommandExecutor>> entry : commandManager.getPluginCommandExecutors().entrySet()) {
                    final XiaomingPlugin key = entry.getKey();
                    // 如果该用户没有禁用该插件，且本群也没禁用
                    if (Objects.isNull(account) || !account.isBlockPlugin(key.getName())) {
                        for (CommandExecutor executor : entry.getValue()) {
                            finalCommandExecutor = executor;
                            if (executor.onCommand(user)) {
                                // 记录日志
                                final String eventMessage = "执行插件 " + key.getCompleteName() + " 的指令：" + user.getMessage();
                                getLogger().info(user.getCompleteName() + " " + eventMessage);
                                user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                                user.getOrPutAccount().readySave();
                                return true;
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of PrivateXiaomingUser or GroupXiaomingUser");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            reportExceptionToLog(user, exception);
            return true;
        }

        // 寻找插件直接交互
        XiaomingPlugin finalXiaomingPlugin = null;
        try {
            final PluginManager pluginManager = getXiaomingBot().getPluginManager();
            if (user instanceof GroupXiaomingUser) {
                final Group group = getXiaomingBot().getGroupManager().forGroup(((GroupXiaomingUser) user).getGroup());
                for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
                    finalXiaomingPlugin = plugin;
                    if ((Objects.isNull(account) || !account.isBlockPlugin(plugin.getName())) &&
                            !group.isUnablePlugin(plugin.getName()) &&
                            plugin.onMessage(user)) {
                        // 记录日志
                        final String eventMessage = "与插件 " + plugin.getCompleteName() + " 交互：" + user.getMessage();
                        getLogger().info(user.getCompleteName() + " " + eventMessage);
                        user.addEvent(AccountEventImpl.groupEvent((GroupXiaomingUser) user, eventMessage));
                        user.getOrPutAccount().readySave();
                        return true;
                    }
                }
            } else if (user instanceof PrivateXiaomingUser) {
                for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
                    finalXiaomingPlugin = plugin;
                    if ((Objects.isNull(account) || !account.isBlockPlugin(plugin.getName())) &&
                            plugin.onMessage(user)) {
                        // 记录日志
                        final String eventMessage = "与插件 " + plugin.getCompleteName() + " 交互：" + user.getMessage();
                        getLogger().info(user.getCompleteName() + " " + eventMessage);
                        user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                        user.getOrPutAccount().readySave();
                        return true;
                    }
                }
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of PrivateXiaomingUser or GroupXiaomingUser");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            reportExceptionToLog(user, exception);
            return true;
        }

        // 寻找已有的交互器
        final Interactor lastInteractor = user.getInteractor();
        if (Objects.nonNull(lastInteractor)) {
            if (!lastInteractor.isWillExit(user.getQQ())) {
                try {
                    // 记录日志
                    final String eventMessage = "继续与交互器 " + lastInteractor.getClass().getName() + " 交互：" + user.getMessage();
                    getLogger().info(user.getCompleteName() + " " + eventMessage);
                    if (user instanceof GroupXiaomingUser) {
                        user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
                    } else {
                        user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                    }
                    user.getOrPutAccount().readySave();

                    // 交互
                    lastInteractor.interact(user);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                            getXiaomingBot().getEmojiManager().get("happy"));

                    reportExceptionToLog(user, exception);
                    lastInteractor.setWillExit(user.getQQ());
                    user.setInteractor(null);
                }
                return true;
            } else {
                user.setInteractor(null);
            }
        }

        // 寻找新的交互器
        Interactor finalInteractor = null;
        try {
            final InteractorManager manager = getXiaomingBot().getInteractorManager();
            final Set<Interactor> coreInteractors = manager.getCoreInteractors();
            final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = manager.getPluginInteractors();
            for (Interactor interactor : coreInteractors) {
                finalInteractor = interactor;
                if (interactor.willInteract(user)) {
                    user.setInteractor(interactor);
                    interactor.interact(user);
                    // 记录日志
                    final String eventMessage = "初次与内核交互器 " + interactor.getClass().getName() + " 交互：" + user.getMessage();
                    getLogger().info(user.getCompleteName() + " " + eventMessage);
                    if (user instanceof GroupXiaomingUser) {
                        user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
                    } else {
                        user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                    }
                    user.getOrPutAccount().readySave();
                    return true;
                }
            }
            if (user instanceof GroupXiaomingUser) {
                final Group group = getXiaomingBot().getGroupManager().forGroup(((GroupXiaomingUser) user).getGroup());
                for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                    if (!group.isUnablePlugin(entry.getKey().getName()) &&
                            (Objects.isNull(account) || !account.isBlockPlugin(entry.getKey().getName()))) {
                        for (Interactor interactor : entry.getValue()) {
                            finalInteractor = interactor;
                            if (interactor.willInteract(user)) {
                                // 记录日志
                                final String eventMessage = "初次与插件 " + entry.getKey().getCompleteName() + " 交互器 " + interactor.getClass().getName() + " 交互：" + user.getMessage();
                                getLogger().info(user.getCompleteName() + " " + eventMessage);
                                user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
                                user.getOrPutAccount().readySave();

                                user.setInteractor(interactor);
                                interactor.interact(user);
                                return true;
                            }
                        }
                    }
                }
            } else if (user instanceof PrivateXiaomingUser) {
                for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                    if ((Objects.isNull(account) || !account.isBlockPlugin(entry.getKey().getName()))) {
                        for (Interactor interactor : entry.getValue()) {
                            finalInteractor = interactor;
                            if (interactor.willInteract(user)) {
                                // 记录日志
                                final String eventMessage = "初次与 " + entry.getKey().getCompleteName() + " 交互器 " + interactor.getClass().getName() + " 交互：" + user.getMessage();
                                getLogger().info(user.getCompleteName() + " " + eventMessage);
                                user.addEvent(AccountEventImpl.privateEvent(eventMessage));
                                user.getOrPutAccount().readySave();

                                user.setInteractor(interactor);
                                interactor.interact(user);
                                return true;
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of PrivateXiaomingUser or GroupXiaomingUser");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            reportExceptionToLog(user, exception);
            if (Objects.nonNull(finalInteractor)) {
                finalInteractor.setWillExit(user.getQQ());
            }
            user.setInteractor(null);
            return true;
        }

        onInteractorNotFound(user);
        return false;
    }

    private void reportToLog(final String message) {
        for (Group log : getXiaomingBot().getGroupManager().forTag("log")) {
            getXiaomingBot().getMsgSender().SENDER.sendGroupMsg(log.getCode(), message);
        }
    }

    @Override
    public void reportExceptionToLog(@NotNull final DispatcherUser user,
                                     @NotNull final Exception exception) {
        // 记录日志
        final String eventMessage = "触发异常：" + exception;
        if (user instanceof GroupXiaomingUser) {
            user.addEvent(AccountEventImpl.groupEvent(((GroupXiaomingUser) user), eventMessage));
        } else {
            user.addEvent(AccountEventImpl.privateEvent(eventMessage));
        }
        getLogger().error(user.getCompleteName() + " " + eventMessage);

        // 通知日志群
        reportToLog("出现一条新的异常信息，输入 #近期异常 以查看");
        final Throwable cause = exception.getCause();
        final String message = Objects.nonNull(cause) ? cause.toString() : exception.toString();

        if (user instanceof GroupXiaomingUser) {
            errorMessageManager.addErrorMessage(new ErrorMessageImpl(user.getQQ(),
                    user.getName(), ((GroupXiaomingUser) user).getGroup(), ((GroupXiaomingUser) user).getGroupInfo().getGroupName(), message, user.getRecentInputs()));
        } else if (user instanceof PrivateXiaomingUser) {
            errorMessageManager.addErrorMessage(new ErrorMessageImpl(user.getQQ(), user.getName(), message, user.getRecentInputs()));
        } else {
            throw new IllegalArgumentException("dispatcher user must be instance of GroupXiaomingUser or PrivateXiaomingUser");
        }
        exception.printStackTrace();
        errorMessageManager.readySave();
    }

    @Override
    public void onInteractorNotFound(@NotNull final UserDataType user) {
    }
}