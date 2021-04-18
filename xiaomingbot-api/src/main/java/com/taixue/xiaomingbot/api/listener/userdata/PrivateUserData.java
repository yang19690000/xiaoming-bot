package com.taixue.xiaomingbot.api.listener.userdata;

import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;

public interface PrivateUserData {
    PrivateMsg getPrivateMsg();

    default long getQQ() {
        return getAccountInfo().getAccountCodeNumber();
    }

    default String getQQString() {
        return getAccountInfo().getAccountCode();
    }

    default AccountInfo getAccountInfo() {
        return getPrivateMsg().getAccountInfo();
    }

    void sendPrivateMessage(String message, Object... arguments);
}
