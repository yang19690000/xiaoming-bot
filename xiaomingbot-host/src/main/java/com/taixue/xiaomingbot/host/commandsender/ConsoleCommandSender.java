package com.taixue.xiaomingbot.host.commandsender;

import com.taixue.xiaomingbot.api.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleCommandSender extends CommandSender {
    protected final Logger LOGGER = LoggerFactory.getLogger(">> Console");

    public ConsoleCommandSender() {
        super(CONSOLE_NAME);
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        LOGGER.info(message);
    }

    @Override
    public void sendError(String message) {
        LOGGER.error(message);
    }

    @Override
    public void sendWarn(String message) {
        LOGGER.warn(message);
    }
}
