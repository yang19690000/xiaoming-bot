package com.taixue.xiaomingbot.host.command.sender;

import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.api.command.CommandSender;
import love.forte.simbot.api.sender.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleCommandSender extends CommandSender {
    protected final Logger LOGGER = LoggerFactory.getLogger("Console");

    public ConsoleCommandSender() {
        super(CONSOLE_NAME);
    }

    @Override
    public boolean hasPermission(String node) {
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

    @Override
    public XiaomingBot getXiaomingBot() {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance();
    }
}
