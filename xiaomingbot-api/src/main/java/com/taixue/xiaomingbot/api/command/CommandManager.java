package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandManager {
    protected List<CommandExecutor> commandExecutors = new ArrayList<>();

    public boolean onCommand(CommandSender sender, String input) {
        if (!execute(sender, input)) {
            onUnknownCommand(sender, input);
            return false;
        }
        else {
            return true;
        }
    }

    public void onUnknownCommand(CommandSender sender, String input) {}

    public boolean execute(CommandSender sender, String input) {
        for (CommandExecutor executor: commandExecutors) {
            if (executor.onCommand(sender, input)) {
                return true;
            }
        }
        return false;
    }

    public void register(CommandExecutor executor, XiaomingPlugin plugin) {
        executor.setPlugin(plugin);
        commandExecutors.add(executor);
    }

    public void onUloadPlugin(XiaomingPlugin plugin) {
        commandExecutors.removeIf(executor -> executor.getPlugin() == plugin);
    }
}
