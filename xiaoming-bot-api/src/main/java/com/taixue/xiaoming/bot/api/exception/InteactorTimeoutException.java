package com.taixue.xiaoming.bot.api.exception;

import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;

public class InteactorTimeoutException extends XiaomingRuntimeException {
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
