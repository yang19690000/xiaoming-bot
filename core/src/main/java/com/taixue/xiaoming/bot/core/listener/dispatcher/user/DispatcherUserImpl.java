package com.taixue.xiaoming.bot.core.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.core.user.QQXiaomingUserImpl;

public abstract class DispatcherUserImpl extends QQXiaomingUserImpl implements DispatcherUser {
    Interactor interactor;

    @Override
    public Interactor getInteractor() {
        return interactor;
    }

    @Override
    public void setInteractor(Interactor interactor) {
        this.interactor = interactor;
    }
}
