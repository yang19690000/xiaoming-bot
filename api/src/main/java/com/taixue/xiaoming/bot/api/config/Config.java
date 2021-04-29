package com.taixue.xiaoming.bot.api.config;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;

public interface Config extends FileSavedData {
    boolean isDebug();

    void setDebug(boolean debug);

    CallLimitConfig getGroupCallConfig();

    CallLimitConfig getPrivateCallConfig();
}
