package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;

public class PrivateDispatcherUserData extends DispatcherUserData {
    protected PrivateInteractor interactor;

    public PrivateInteractor getInteractor() {
        return interactor;
    }

    public void setInteractor(PrivateInteractor interactor) {
        this.interactor = interactor;
    }
}