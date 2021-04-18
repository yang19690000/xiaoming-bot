package com.taixue.xiaomingbot.api.listener.userdata;
import java.util.Objects;

public class MessageWaiter {
    private long endTime;
    private volatile String value;
    private String defaultValue;
    private boolean result;

    public MessageWaiter(long endTime, String defaultValue) {
        this.endTime = endTime;
        this.defaultValue = defaultValue;
    }

    public void onInput(String value) {
        if (System.currentTimeMillis() < endTime) {
            this.value = value;
            synchronized (this) {
                notify();
            }
        }
    }

    public String getValue() {
        return Objects.nonNull(value) ? value : defaultValue;
    }
}
