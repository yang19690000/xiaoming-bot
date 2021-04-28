package com.taixue.xiaoming.bot.api.config;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

import java.util.List;

public interface Config extends FileSavedData {
    boolean isDebug();

    void setDebug(boolean debug);

    long getCallCounter();

    void increaseCallCounter();

    List<BotAccount> getAccounts();
}
