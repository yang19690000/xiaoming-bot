package com.taixue.xiaoming.bot.api.listener.interactor.user;

import com.taixue.xiaoming.bot.api.user.PrivateXiaomingUser;
import love.forte.simbot.api.message.events.PrivateMsg;

public interface PrivateInteractorUser extends InteractorUser, PrivateXiaomingUser {
    void setPrivateMsg(PrivateMsg privateMsg);

    PrivateMsg getPrivateMsg();
}
