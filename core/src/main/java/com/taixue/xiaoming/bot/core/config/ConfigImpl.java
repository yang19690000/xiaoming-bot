package com.taixue.xiaoming.bot.core.config;

import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

import java.util.ArrayList;
import java.util.List;

public class ConfigImpl extends JsonFileSavedData implements Config {
    private volatile boolean debug = false;
    private volatile long callCounter = 0;
    private List<BotAccount> accounts = new ArrayList<>();

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Override
    public long getCallCounter() {
        return callCounter;
    }

    @Override
    public void increaseCallCounter() {
        callCounter++;
        save();
    }

    @Override
    public List<BotAccount> getAccounts() {
        return accounts;
    }

    public void setCallCounter(long callCounter) {
        this.callCounter = callCounter;
    }

    public void setAccounts(List<BotAccount> accounts) {
        this.accounts = accounts;
    }
}
