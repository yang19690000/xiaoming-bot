package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import love.forte.simbot.api.sender.MsgSender;

public interface DispatcherUser extends QQXiaomingUser {
    Interactor getInteractor();

    void setInteractor(Interactor interactor);

    @Override
    String getMessage();

    @Override
    boolean hasPermission(String node);
}
