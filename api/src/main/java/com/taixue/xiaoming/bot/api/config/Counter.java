package com.taixue.xiaoming.bot.api.config;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

public interface Counter extends FileSavedData {
    long getCallCounter();

    void increaseCallCounter();
}
