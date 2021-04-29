package com.taixue.xiaoming.bot.core.config;

import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallLimiter;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.core.limit.CallLimitConfigImpl;
import com.taixue.xiaoming.bot.core.limit.UserCallLimiterImpl;

public class ConfigImpl extends JsonFileSavedData implements Config {
    private boolean debug = false;
    private CallLimitConfig groupCallLimiter = new CallLimitConfigImpl();
    private CallLimitConfig privateCallLimiter = new CallLimitConfigImpl();

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Override
    public CallLimitConfig getGroupCallConfig() {
        return groupCallLimiter;
    }

    @Override
    public CallLimitConfig getPrivateCallConfig() {
        return privateCallLimiter;
    }
}
