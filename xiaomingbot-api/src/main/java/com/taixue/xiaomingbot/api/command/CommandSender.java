package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.base.XiaomingObject;

public abstract class CommandSender extends XiaomingObject {
    public static final String CONSOLE_NAME = "CONSOLE";

    private final String name;

    public CommandSender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean hasPermission(String node);

    public abstract void sendMessage(String message, Object... arguments);

    public void sendError(String message, Object... arguments) {
        sendMessage("(。﹏。*)" + message, arguments);
    }

    public void sendWarn(String message, Object... arguments) {
        sendMessage("Σ(っ °Д °;)っ" + message, arguments);
    }
}
