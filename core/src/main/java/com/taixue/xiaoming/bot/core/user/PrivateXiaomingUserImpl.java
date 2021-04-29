package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.PrivateXiaomingUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;

/**
 * @author Chuanwise
 */
public class PrivateXiaomingUserImpl extends QQXiaomingUserImpl implements PrivateXiaomingUser {
    private PrivateMsg privateMsg;

    public PrivateMsg getPrivateMsg() {
        return privateMsg;
    }

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
}
