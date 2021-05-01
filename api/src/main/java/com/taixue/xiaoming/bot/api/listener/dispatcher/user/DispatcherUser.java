package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountEvent;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import love.forte.simbot.api.sender.MsgSender;
import net.mamoe.mirai.event.events.UserEvent;

public interface DispatcherUser extends QQXiaomingUser {
    Interactor getInteractor();

    void setInteractor(Interactor interactor);

    @Override
    String getMessage();

    default void addRecentInput() {
        getRecentInputs().add(getMessage());
    }

    default void addEvent(AccountEvent event) {
        getOrPutAccount().addEvent(event);
    }
}
