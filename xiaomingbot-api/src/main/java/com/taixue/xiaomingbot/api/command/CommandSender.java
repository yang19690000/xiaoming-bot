package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.base.XiaomingObject;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;

public abstract class CommandSender extends XiaomingObject {
    public static final String CONSOLE_NAME = "CONSOLE";

    protected final String name;

    public CommandSender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean hasPermission(String node);

    public abstract void sendMessage(String message);

    public void sendError(String message) {
        sendMessage("(。﹏。*) " + message);
    }

    public void sendWarn(String message) {
        sendMessage("Σ(っ °Д °;)っ " + message);
    }

    public void sendMessage(String format, Object... arguments) {
        sendMessage(replaceArguments(format, arguments));
    }

    public void sendError(String format, Object... arguments) {
        sendError(replaceArguments(format, arguments));
    }

    public void sendWarn(String format, Object... arguments) {
        sendWarn(replaceArguments(format, arguments));
    }

    protected String replaceArguments(String format, Object... arguments) {
        StringBuilder builder = new StringBuilder(format);
        for (Object argument: arguments) {
            int pos = builder.indexOf("{}");
            if (pos != -1) {
                builder.replace(pos, pos + 2, argument.toString());
            }
            else {
                break;
            }
        }
        return builder.toString();
    }
}
