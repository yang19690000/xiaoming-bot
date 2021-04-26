package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUserImpl;

public abstract class DispatcherUser extends QQXiaomingUserImpl {
    Interactor interactor;

    public Interactor getInteractor() {
        return interactor;
    }

    public void setInteractor(Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    protected abstract void sendMessage(final String message);

    public abstract String getMessage();

    @Override
    public abstract boolean hasPermission(final String node);
}
