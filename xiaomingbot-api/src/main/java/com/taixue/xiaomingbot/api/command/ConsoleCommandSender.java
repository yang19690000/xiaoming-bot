package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.util.ArgumentUtil;

public abstract class ConsoleCommandSender extends CommandSender {
    public ConsoleCommandSender() {
        super(CONSOLE_NAME);
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        getLogger().info(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendError(String message, Object... arguments) {
        getLogger().error(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendWarn(String message, Object... arguments) {
        getLogger().error(ArgumentUtil.replaceArguments(message, arguments));
    }
}
