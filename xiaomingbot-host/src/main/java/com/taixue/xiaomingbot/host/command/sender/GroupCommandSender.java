package com.taixue.xiaomingbot.host.command.sender;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.util.ArgumentUtil;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public class GroupCommandSender extends com.taixue.xiaomingbot.api.command.GroupCommandSender {
    private MsgSender msgSender;

    public GroupCommandSender(GroupMsg groupMsg, MsgSender msgSender) {
        super(groupMsg);
        this.msgSender = msgSender;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return com.taixue.xiaomingbot.host.XiaomingBot.getInstance();
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        msgSender.SENDER.sendGroupMsg(getGroupInfo(),
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
