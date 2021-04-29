package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.core.record.SizedRecorderImpl;
import org.jetbrains.annotations.NotNull;

public class UserCallSizedRecord extends SizedRecorderImpl<Long> {
    @NotNull
    @Override
    public Long[] list() {
        return getRecords().toArray(new Long[0]);
    }
}
