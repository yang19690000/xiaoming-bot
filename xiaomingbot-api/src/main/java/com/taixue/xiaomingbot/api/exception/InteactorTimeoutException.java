package com.taixue.xiaomingbot.api.exception;

import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.interactor.Interactor;

public class InteactorTimeoutException extends XiaomingException {
    private Interactor interactor;

    public InteactorTimeoutException(Interactor interactor) {
        setInteractor(interactor);
    }

    public void setInteractor(Interactor interactor) {
        this.interactor = interactor;
    }

    public Interactor getInteractor() {
        return interactor;
    }
}
