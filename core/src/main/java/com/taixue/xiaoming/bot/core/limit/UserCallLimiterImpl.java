package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.UserCallLimiter;
import com.taixue.xiaoming.bot.api.limit.UserCallRecord;
import org.jetbrains.annotations.NotNull;

public class UserCallLimiterImpl extends CallLimiterImpl<Long, UserCallRecord> implements UserCallLimiter {
    @NotNull
    @Override
    public UserCallRecord newRecordInstance() {
        return new UserCallRecordImpl();
    }
}
