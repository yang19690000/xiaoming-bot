package com.taixue.xiaoming.bot.api.limit;

import org.jetbrains.annotations.NotNull;

public interface UserCallLimiter extends CallLimiter<Long, UserCallRecord> {
    @NotNull
    UserCallRecord newRecordInstance();
}
