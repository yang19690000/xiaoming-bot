package com.taixue.xiaoming.bot.core.listener.interactor.user;

import com.taixue.xiaoming.bot.api.listener.interactor.user.InteractorUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.MessageWaiter;
import com.taixue.xiaoming.bot.core.user.QQXiaomingUserImpl;

public abstract class InteractorUserImpl extends QQXiaomingUserImpl implements InteractorUser {
    private boolean exit;
    private MessageWaiter messageWaiter;
    private boolean firstInteract = true;

    @Override public MessageWaiter getMessageWaiter() {
        return messageWaiter;
    }

    @Override public void setMessageWaiter(MessageWaiter messageWaiter) {
        this.messageWaiter = messageWaiter;
    }

    @Override public boolean isExit() {
        return exit;
    }

    @Override public void shouldExit() {
        exit = true;
    }

    @Override public boolean isFirstInteract() {
        return firstInteract;
    }

    @Override public void setFirstInteract(boolean firstInteract) {
        this.firstInteract = firstInteract;
    }
}
