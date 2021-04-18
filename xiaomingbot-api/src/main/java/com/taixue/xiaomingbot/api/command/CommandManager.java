package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.base.XiaomingObject;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager {
    private List<CommandExecutor> commandExecutors = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public boolean onCommand(CommandSender sender, String input) {
        if (!execute(sender, input)) {
            onUnknownCommand(sender, input);
            return false;
        }
        else {
            return true;
        }
    }

    public List<CommandExecutor> getCommandExecutors() {
        return commandExecutors;
    }

    public void onUnknownCommand(CommandSender sender, String input) {}

    public boolean execute(CommandSender sender, String input) {
        for (CommandExecutor executor: commandExecutors) {
            // 属于核心指令
            if (Objects.isNull(executor.getPlugin())) {
                if (executor.onCommand(sender, input)) {
                    return true;
                }
            }
            else if (sender instanceof GroupCommandSender) {
                if (!sender.getXiaomingBot().getPluginConfig().unableInGroup(executor.getPlugin().getName(),
                        ((GroupCommandSender) sender).getGroupCode()) && executor.onCommand(sender, input)) {
                    return true;
                }
            }
            else if (executor.onCommand(sender, input)) {
                return true;
            }
        }
        return false;
    }

    public Logger getLogger() {
        return logger;
    }

    public void register(CommandExecutor executor, XiaomingPlugin plugin) {
        if (Objects.isNull(plugin)) {
            getLogger().info("正在注册内核指令处理器：{}", executor.getClass().getName());
        }
        else {
            getLogger().info("正在注册来自 {} 插件的指令处理器：{}", plugin.getCompleteName(), executor.getClass().getName());
        }
        executor.setPlugin(plugin);
        executor.reloadSubcommandExecutor();
        commandExecutors.add(executor);
    }

    public void unloadPlugin(XiaomingPlugin plugin) {
        commandExecutors.removeIf(executor -> executor.getPlugin() == plugin);
    }

    public void unloadAll() {
        commandExecutors.clear();
    }
}
