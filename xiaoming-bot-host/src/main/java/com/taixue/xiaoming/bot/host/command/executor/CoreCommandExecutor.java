package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.exception.XiaomingRuntimeException;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CoreCommandExecutor extends CommandExecutor {
    public String getCommandExecutorsString() {
        StringBuilder builder = new StringBuilder();

        final CommandManager commandManager = getXiaomingBot().getCommandManager();
        final Set<CommandExecutor> coreCommandExecutors = commandManager.getCoreCommandExecutors();
        final Map<XiaomingPlugin, Set<CommandExecutor>> pluginCommandExecutors = commandManager.getPluginCommandExecutors();

        builder.append("内核指令处理器：");
        if (coreCommandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (CommandExecutor coreCommandExecutor : coreCommandExecutors) {
                builder.append("\n").append(coreCommandExecutor.getClass().getName());
            }
        }
        builder.append("\n");

        builder.append("插件指令处理器：");
        if (pluginCommandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Map.Entry<XiaomingPlugin, Set<CommandExecutor>> entry : pluginCommandExecutors.entrySet()) {
                builder.append("\n")
                        .append("由").append(entry.getKey().getCompleteName()).append("注册：");
                for (CommandExecutor commandExecutor : entry.getValue()) {
                    builder.append("\n")
                            .append("> ").append(commandExecutor.getClass().getName());
                }
            }
        }
        return builder.toString();
    }

    public String getInteractorString() {
        StringBuilder builder = new StringBuilder();

        final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = getXiaomingBot().getInteractorManager().getPluginInteractors();
        builder.append("交互器：");
        if (pluginInteractors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                builder.append("\n")
                        .append("由").append(entry.getKey().getCompleteName()).append("注册：");
                for (Interactor interactor : entry.getValue()) {
                    builder.append("\n")
                            .append("> ").append(interactor.getClass().getName());
                }
            }
        }
        return builder.toString();
    }

    public String getLoadedPluginString() {
        StringBuilder builder = new StringBuilder();

        final Set<XiaomingPlugin> loadedPlugins = getXiaomingBot().getPluginManager().getLoadedPlugins();
        builder.append("加载的插件：");
        if (loadedPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin value : loadedPlugins) {
                builder.append("\n").append(value.getCompleteName());
            }
        }
        return builder.toString();
    }

    public String getPluginMessage(final XiaomingPlugin plugin) {
        StringBuilder builder = new StringBuilder();
        builder.append("插件：").append(plugin.getCompleteName());
        builder.append("\n");

        final Set<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        builder.append("交互器：");
        if (Objects.isNull(interactors) || interactors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Interactor interactor : interactors) {
                builder.append("\n")
                        .append(interactor.getClass().getName());
            }
        }
        builder.append("\n");

        final Set<CommandExecutor> commandExecutors = getXiaomingBot().getCommandManager().getPluginCommandExecutors(plugin);
        builder.append("指令处理器：");
        if (Objects.isNull(commandExecutors) || commandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (CommandExecutor executor : commandExecutors) {
                builder.append("\n")
                        .append(executor.getClass().getName());
            }
        }

        return builder.toString();
    }

    @CommandFormat(CommandWordUtil.PLUGIN_REGEX)
    @RequiredPermission("plugin.list")
    public void onLoadedPluginsStatus(final XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("插件详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getLoadedPluginString());
        } else {
            user.sendMessage(getLoadedPluginString());
        }
    }

    @CommandFormat("(维护|调试|debug)")
    @RequiredPermission("debug")
    public void onDebug(final XiaomingUser user) {
        final Config config = getXiaomingBot().getConfig();
        config.setDebug(!config.isDebug());
        config.save();
        if (config.isDebug()) {
            user.sendMessage("已开启小明的维护状态");
        } else {
            user.sendMessage("已关闭小明的维护状态");
        }
    }

    @CommandFormat(CommandWordUtil.COMMAND_EXECUTOR_REGEX)
    @RequiredPermission("commandexecutor.list")
    public void onCommandExecutorStatus(final XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("指令处理器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getCommandExecutorsString());
        } else {
            user.sendMessage(getCommandExecutorsString());
        }
    }

    @CommandFormat("(交互器|interactor)")
    @RequiredPermission("interactor.list")
    public void onInteractorStatus(final XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("交互器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getInteractorString());
        } else {
            user.sendMessage(getInteractorString());
        }
    }

    @CommandFormat("调用")
    @CommandFormat("call")
    @CommandFormat("调用查询")
    public void onCallCounter(final XiaomingUser sender) {
        sender.sendMessage("调用次数：{}", getXiaomingBot().getConfig().getCallCounter());
    }

    @CommandFormat("(异常|exception)")
    @RequiredPermission("debug.exception")
    public void onThrowException(final XiaomingUser sender) {
        sender.sendMessage("小明将尝试抛出异常：XiaomingRuntimeException");
        throw new XiaomingRuntimeException();
    }
}