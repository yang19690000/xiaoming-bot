package com.taixue.xiaomingbot.api.listener.userdata;

import catcode.CatCodeUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;

public interface GroupUserData {
    GroupMsg getGroupMsg();

    default long getQQ() {
        return getAccountInfo().getAccountCodeNumber();
    }

    default AccountInfo getAccountInfo() {
        return getGroupMsg().getAccountInfo();
    }

    default String getQQString() {
        return getAccountInfo().getAccountCode();
    }

    default long getGroup() {
        return getGroupInfo().getGroupCodeNumber();
    }

    default String getGroupString() {
        return getGroupInfo().getGroupCode();
    }

    default GroupInfo getGroupInfo() {
        return getGroupMsg().getGroupInfo();
    }

    default String at() {
        return CatCodeUtil.getInstance().getStringTemplate().at(getQQ());
    }

    void sendGroupMessage(String message, Object... arguments);

    void sendPrivateMessage(String message, Object... arguments);

    default void atSendGroupMessage(String message, Object... arguments) {
        sendGroupMessage(at() + message, arguments);
    }
}
