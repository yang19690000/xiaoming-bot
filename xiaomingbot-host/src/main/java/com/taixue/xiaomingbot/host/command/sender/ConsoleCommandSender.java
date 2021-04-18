package com.taixue.xiaomingbot.host.command.sender;

import com.taixue.xiaomingbot.host.XiaomingBot;

public class ConsoleCommandSender extends com.taixue.xiaomingbot.api.command.ConsoleCommandSender {
    @Override
    public XiaomingBot getXiaomingBot() {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance();
    }
}
