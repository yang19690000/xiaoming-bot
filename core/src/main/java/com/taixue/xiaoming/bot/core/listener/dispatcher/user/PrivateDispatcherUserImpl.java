package com.taixue.xiaoming.bot.core.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

public class PrivateDispatcherUserImpl extends DispatcherUserImpl implements PrivateDispatcherUser {
    private PrivateMsg privateMsg;

    @Override
    public PrivateMsg getPrivateMsg() {
        return privateMsg;
    }

    @Override
    public void setPrivateMsg(PrivateMsg privateMsg) {
        this.privateMsg = privateMsg;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return privateMsg.getAccountInfo();
    }

    @Override
    public String getMessage() {
        return privateMsg.getMsg();
    }

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }
}