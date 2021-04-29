package com.taixue.xiaoming.bot.core.config;

import com.taixue.xiaoming.bot.api.config.Counter;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

public class CounterImpl extends JsonFileSavedData implements Counter {
    private volatile long callCounter = 0;

    @Override
    public long getCallCounter() {
        return callCounter;
    }

    @Override
    public void increaseCallCounter() {
        callCounter++;
        save();
    }
}
