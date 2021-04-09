package com.taixue.xiaomingbot.host.command.sender;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public class GroupCommandSender extends com.taixue.xiaomingbot.api.command.GroupCommandSender {
    protected MsgSender msgSender;

    public GroupCommandSender(MsgSender msgSender, long group, long qq) {
        super(group, qq);
        this.msgSender = msgSender;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return null;
    }

    @Override
    public void sendMessage(String message) {
        msgSender.SENDER.sendGroupMsg(group, CatCodeUtil.getInstance().getStringTemplate().at(qq) + message);
    }

    @Override
    public boolean hasPermission(String node) {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance().getPermissionSystem().hasPermission(qq, node);
    }

    @Override
    public MsgSender getMsgSender() {
        return msgSender;
    }
}
