package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import catcode.CatCodeUtil;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public class GroupDispatcherUser extends DispatcherUser implements GroupXiaomingUser {
    private MsgSender msgSender;
    private GroupMsg groupMsg;

    @Override
    public AccountInfo getAccountInfo() {
        return groupMsg.getAccountInfo();
    }

    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
    }

    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    @Override
    public MsgSender getMsgSender() {
        return msgSender;
    }

    @Override
    public String getMessage() {
        return groupMsg.getMsg();
    }

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public String getName() {
        return getQQString();
    }

    @Override
    public GroupInfo getGroupInfo() {
        return groupMsg.getGroupInfo();
    }

    @Override
    protected void sendMessage(String message) {
        sendGroupMessage(CatCodeUtil.getInstance().getStringTemplate().at(getQQString()) + " " + message);
    }

    public GroupMsg getGroupMsg() {
        return groupMsg;
    }
}
