package com.taixue.xiaoming.bot.core.listener.dispatcher;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.listener.dispatcher.Dispatcher;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.PrivateXiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;
import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Chuanwise
 */
public class DispatcherImpl<UserDataType extends DispatcherUser> extends HostObjectImpl implements Dispatcher<UserDataType> {
    @Override
    public boolean onMessage(UserDataType user) {
        final String message = user.getMessage();
        final Account account = getXiaomingBot().getAccountManager().getAccount(user.getQQ());

        CommandExecutor finalCommandExecutor = null;
        try {
            final CommandManager commandManager = getXiaomingBot().getCommandManager();
            for (CommandExecutor executor : commandManager.getCoreCommandExecutors()) {
                finalCommandExecutor = executor;
                if (executor.onCommand(user)) {
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
                                return true;
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of PrivateXiaomingUser or GroupXiaomingUser");
            }
        } catch (TimeoutCancellationException exception) {
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            if (Objects.nonNull(finalCommandExecutor)) {
                reportExceptionToLog(user, exception, finalCommandExecutor, user.getMsgSender());
            }
            return true;
        }

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
                        return true;
                    }
                }
            } else if (user instanceof PrivateXiaomingUser) {
                for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
                    finalXiaomingPlugin = plugin;
                    if ((Objects.isNull(account) || !account.isBlockPlugin(plugin.getName())) &&
                            plugin.onMessage(user)) {
                        return true;
                    }
                }
            } else {
                throw new IllegalArgumentException("dispatcher user must be instance of PrivateXiaomingUser or GroupXiaomingUser");
            }
        } catch (TimeoutCancellationException exception) {
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            if (Objects.nonNull(finalXiaomingPlugin)) {
                reportExceptionToLog(user, exception, finalXiaomingPlugin, user.getMsgSender());
            }
            return true;
        }

        final Interactor lastInteractor = user.getInteractor();

        // 已经有交互器了
        if (Objects.nonNull(lastInteractor)) {
            if (!lastInteractor.isWillExit(user.getQQ())) {
                try {
                    lastInteractor.interact(user);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                            getXiaomingBot().getEmojiManager().get("happy"));

                    if (Objects.nonNull(lastInteractor)) {
                        reportExceptionToLog(user, exception, lastInteractor, user.getMsgSender());
                    }
                }
                return true;
            } else {
                user.setInteractor(null);
            }
        }

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
                    return true;
                }
            }
            if (Objects.isNull(finalInteractor)) {
                if (user instanceof GroupXiaomingUser) {
                    final Group group = getXiaomingBot().getGroupManager().forGroup(((GroupXiaomingUser) user).getGroup());
                    for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                        if (!group.isUnablePlugin(entry.getKey().getName()) &&
                                (Objects.isNull(account) || !account.isBlockPlugin(entry.getKey().getName()))) {
                            for (Interactor interactor : entry.getValue()) {
                                finalInteractor = interactor;
                                if (interactor.willInteract(user)) {
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
            }
        } catch (TimeoutCancellationException exception) {
        } catch (Exception exception) {
            exception.printStackTrace();
            user.sendError("抱歉小明遇到了一些错误。这个问题已经上报了，期待更好的小明吧 {}",
                    getXiaomingBot().getEmojiManager().get("happy"));

            if (Objects.nonNull(finalInteractor)) {
                reportExceptionToLog(user, exception, finalInteractor, user.getMsgSender());
            }
            return true;
        }

        onInteractorNotFound(user);
        return false;
    }

    @Override
    public void reportExceptionToLog(@NotNull final DispatcherUser user,
                                     @NotNull final Exception exception,
                                     @NotNull final XiaomingPlugin plugin,
                                     @NotNull final MsgSender msgSender) {
        StringBuilder builder = new StringBuilder()
                .append("【出现异常】").append("\n");

        if (user instanceof GroupDispatcherUser) {
            builder.append("插件 ").append(plugin.getCompleteName()).append(" 响应群聊消息时出现异常。").append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("发生群组：").append(((GroupDispatcherUser) user).getGroupString()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        } else {
            builder.append("插件 ").append(plugin.getCompleteName()).append(" 响应私聊消息时出现异常。").append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        }
        final String logString = builder.toString();
        /*
        for (Group log : getXiaomingBot().getGroupManager().forTag("log")) {
            msgSender.SENDER.sendGroupMsg(log.getCode(), logString);
        }
         */
        msgSender.SENDER.sendPrivateMsg(msgSender.GETTER.getBotInfo(), logString);
        exception.printStackTrace();
    }

    @Override
    public void reportExceptionToLog(@NotNull final DispatcherUser user,
                                     @NotNull final Exception exception,
                                     @NotNull final Interactor interactor,
                                     @NotNull final MsgSender msgSender) {
        StringBuilder builder = new StringBuilder()
                .append("【出现异常】").append("\n");

        if (user instanceof GroupDispatcherUser) {
            builder.append("交互器 ").append(interactor.getClass().getSimpleName()).append(" 响应群聊消息时出现异常。").append("\n")
                    .append("注册方：").append(Objects.isNull(interactor.getPlugin()) ? "内核" : interactor.getPlugin().getCompleteName()).append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("发生群组：").append(((GroupDispatcherUser) user).getGroupString()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        } else {
            builder.append("交互器 ").append(interactor.getClass().getSimpleName()).append(" 响应私聊消息时出现异常。").append("\n")
                    .append("注册方：").append(Objects.isNull(interactor.getPlugin()) ? "内核" : interactor.getPlugin().getCompleteName()).append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        }
        final String logString = builder.toString();
        /*
        for (Group log : getXiaomingBot().getGroupManager().forTag("log")) {
            msgSender.SENDER.sendGroupMsg(log.getCode(), logString);
        }
         */
        msgSender.SENDER.sendPrivateMsg(msgSender.GETTER.getBotInfo(), logString);
        exception.printStackTrace();
    }

    @Override
    public void reportExceptionToLog(@NotNull final DispatcherUser user,
                                     @NotNull final Exception exception,
                                     @NotNull final CommandExecutor executor,
                                     @NotNull final MsgSender msgSender) {
        StringBuilder builder = new StringBuilder()
                .append("【出现异常】").append("\n");

        if (user instanceof GroupDispatcherUser) {
            builder.append("指令处理器 ").append(executor.getClass().getSimpleName()).append(" 响应群聊消息时出现异常。").append("\n")
                    .append("注册方：").append(Objects.isNull(executor.getPlugin()) ? "内核" : executor.getPlugin().getCompleteName()).append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("发生群组：").append(((GroupDispatcherUser) user).getGroupString()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        } else {
            builder.append("指令处理器 ").append(executor.getClass().getSimpleName()).append(" 响应私聊消息时出现异常。").append("\n")
                    .append("注册方：").append(Objects.isNull(executor.getPlugin()) ? "内核" : executor.getPlugin().getCompleteName()).append("\n")
                    .append("类型：").append(exception.getClass().getSimpleName()).append("\n")
                    .append("信息：").append(Objects.isNull(exception.getMessage()) ? "（无）" : exception.getMessage()).append("\n")
                    .append("触发人：").append(user.getAccountInfo().getAccountRemarkOrNickname()).append("（").append(user.getQQString()).append("）").append("\n")
                    .append("输入：").append(user.getMessage()).append("\n")
                    .append("时间：").append(TimeUtil.FORMAT.format(System.currentTimeMillis())).append("\n");
        }
        final String logString = builder.toString();
        /*
        for (Group log : getXiaomingBot().getGroupManager().forTag("log")) {
            msgSender.SENDER.sendGroupMsg(log.getCode(), logString);
        }
         */
        msgSender.SENDER.sendPrivateMsg(msgSender.GETTER.getBotInfo(), logString);
        exception.printStackTrace();
    }

    @Override
    public void onInteractorNotFound(@NotNull final UserDataType user) {
    }
}