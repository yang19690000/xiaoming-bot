package com.taixue.xiaoming.bot.core.listener.interactor.user;

import com.taixue.xiaoming.bot.api.listener.interactor.user.GroupInteractorUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

public class GroupInteractorUserImpl extends InteractorUserImpl implements GroupInteractorUser {
    private MsgSender msgSender;
    private GroupMsg groupMsg;

    @Override
    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    @Override public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return groupMsg.getAccountInfo();
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
    public GroupInfo getGroupInfo() {
        return groupMsg.getGroupInfo();
    }

    @Override
    protected void sendMessage(String message) {
        sendGroupMessage(message);
    }

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public String getName() {
        return getQQString();
    }

    @Override public GroupMsg getGroupMsg() {
        return groupMsg;
    }
}