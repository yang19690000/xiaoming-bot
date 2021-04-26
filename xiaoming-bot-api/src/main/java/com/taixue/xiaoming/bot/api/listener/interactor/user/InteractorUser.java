package com.taixue.xiaoming.bot.api.listener.interactor.user;

import com.taixue.xiaoming.bot.api.user.QQXiaomingUserImpl;

public abstract class InteractorUser extends QQXiaomingUserImpl {
    private boolean exit;
    private MessageWaiter messageWaiter;
    private boolean firstInteract = true;

    public MessageWaiter getMessageWaiter() {
        return messageWaiter;
    }

    public void setMessageWaiter(MessageWaiter messageWaiter) {
        this.messageWaiter = messageWaiter;
    }

    public boolean isExit() {
        return exit;
    }

    public void shouldExit() {
        exit = true;
    }

    public abstract String getMessage();

    public boolean isFirstInteract() {
        return firstInteract;
    }

    public void setFirstInteract(boolean firstInteract) {
        this.firstInteract = firstInteract;
    }
}
