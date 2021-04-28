package com.taixue.xiaoming.bot.core.listener.interactor.user;
import com.taixue.xiaoming.bot.api.listener.interactor.user.MessageWaiter;

import java.util.Objects;

public class MessageWaiterImpl implements MessageWaiter {
    private long endTime;
    private volatile String value;
    private String defaultValue;
    private boolean result;

    public MessageWaiterImpl(long endTime, String defaultValue) {
        this.endTime = endTime;
        this.defaultValue = defaultValue;
    }

    @Override public void onInput(String value) {
        if (System.currentTimeMillis() < endTime) {
            this.value = value;
            synchronized (this) {
                notify();
            }
        }
    }

    @Override public String getValue() {
        return Objects.nonNull(value) ? value : defaultValue;
    }
}
