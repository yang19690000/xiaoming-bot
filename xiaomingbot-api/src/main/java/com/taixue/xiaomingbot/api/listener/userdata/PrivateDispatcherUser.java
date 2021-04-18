package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.util.ArgumentUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

public class PrivateDispatcherUser extends DispatcherUser implements PrivateUserData {
    private PrivateInteractor interactor;
    private PrivateMsg privateMsg;
    private MsgSender msgSender;

    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    public MsgSender getMsgSender() {
        return msgSender;
    }

    public String getMessage() {
        return privateMsg.getMsg();
    }

    @Override
    public PrivateMsg getPrivateMsg() {
        return privateMsg;
    }

    @Override
    public void sendPrivateMessage(String message, Object... arguments) {
        msgSender.SENDER.sendPrivateMsg(getQQ(), ArgumentUtil.replaceArguments(message, arguments));
    }

    public void setPrivateMsg(PrivateMsg privateMsg) {
        this.privateMsg = privateMsg;
    }

    public PrivateInteractor getInteractor() {
        return interactor;
    }

    public void setInteractor(PrivateInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        sendPrivateMessage(message, arguments);
    }

    @Override
    public AccountInfo getAccountInfo() {
        return getPrivateMsg().getAccountInfo();
    }
}