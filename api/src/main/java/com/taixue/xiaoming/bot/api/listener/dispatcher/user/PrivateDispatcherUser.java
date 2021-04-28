package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.user.PrivateXiaomingUser;
import love.forte.simbot.api.message.events.PrivateMsg;

public interface PrivateDispatcherUser extends DispatcherUser, PrivateXiaomingUser {
    PrivateMsg getPrivateMsg();

    void setPrivateMsg(PrivateMsg privateMsg);
}
