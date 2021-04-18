package com.taixue.xiaomingbot.host.command.sender;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.util.ArgumentUtil;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

public class PrivateCommandSender extends com.taixue.xiaomingbot.api.command.PrivateCommandSender {
    private MsgSender msgSender;

    public PrivateCommandSender(PrivateMsg privateMsg, MsgSender msgSender) {
        super(privateMsg);
        this.msgSender = msgSender;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance();
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        msgSender.SENDER.sendPrivateMsg(getAccountInfo(),
                CatCodeUtil.getInstance().getStringTemplate().at(getAccountCode()) +
                        ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public boolean hasPermission(String node) {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance().getPermissionSystem().hasPermission(getAccountCode(), node);
    }

    @Override
    public MsgSender getMsgSender() {
        return msgSender;
    }
}
