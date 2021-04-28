package com.taixue.xiaoming.bot.api.listener.interactor.user;

public interface MessageWaiter {
    void onInput(String value);

    String getValue();
}
