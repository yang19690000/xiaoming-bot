package com.taixue.xiaoming.bot.api.limit;

import com.taixue.xiaoming.bot.api.record.SizedRecorder;

public interface UserCallRecord extends CallRecord {
    SizedRecorder<Long> getRecentCalls();
}
