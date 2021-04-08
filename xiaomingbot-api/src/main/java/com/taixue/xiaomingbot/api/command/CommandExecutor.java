package com.taixue.xiaomingbot.api.command;

public interface CommandExecutor {
    public boolean onCommand(CommandSender sender, String label, String[] arguments);
}
