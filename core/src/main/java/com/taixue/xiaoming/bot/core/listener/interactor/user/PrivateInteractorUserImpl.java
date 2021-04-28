package com.taixue.xiaoming.bot.core.listener.interactor.user;

import com.taixue.xiaoming.bot.api.listener.interactor.user.PrivateInteractorUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

public class PrivateInteractorUserImpl extends InteractorUserImpl implements PrivateInteractorUser {
    private MsgSender msgSender;
    private PrivateMsg privateMsg;

    @Override
    public AccountInfo getAccountInfo() {
        return privateMsg.getAccountInfo();
    }

    @Override
    public MsgSender getMsgSender() {
        return msgSender;
    }

    @Override
    public String getMessage() {
        return privateMsg.getMsg();
    }

    @Override
    protected void sendMessage(String message) {
        sendPrivateMessage(message);
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
    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    @Override public void setPrivateMsg(PrivateMsg privateMsg) {
        this.privateMsg = privateMsg;
    }

    @Override public PrivateMsg getPrivateMsg() {
        return privateMsg;
    }
}
