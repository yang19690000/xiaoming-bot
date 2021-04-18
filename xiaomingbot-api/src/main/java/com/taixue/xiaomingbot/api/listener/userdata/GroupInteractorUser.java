package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.util.ArgumentUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

public class GroupInteractorUser extends InteractorUser implements GroupUserData {
    private GroupMsg groupMsg;
    private MsgSender msgSender;

    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    public MsgSender getMsgSender() {
        return msgSender;
    }

    public String getMessage() {
        return groupMsg.getMsg();
    }

    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
    }

    @Override
    public GroupMsg getGroupMsg() {
        return groupMsg;
    }

    @Override
    public void sendGroupMessage(String message, Object... arguments) {
        msgSender.SENDER.sendGroupMsg(getGroup(), ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendPrivateMessage(String message, Object... arguments) {
        msgSender.SENDER.sendPrivateMsg(getQQ(), ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        atSendGroupMessage(message, arguments);
    }

    @Override
    public AccountInfo getAccountInfo() {
        return getGroupMsg().getAccountInfo();
    }
}
