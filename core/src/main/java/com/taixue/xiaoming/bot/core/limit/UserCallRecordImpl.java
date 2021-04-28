package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallRecord;
import com.taixue.xiaoming.bot.api.record.SizedRecorder;
import com.taixue.xiaoming.bot.core.record.SizedRecorderImpl;
import org.jetbrains.annotations.NotNull;

public class UserCallRecordImpl implements UserCallRecord {
    private SizedRecorder<Long> records = new SizedRecorderImpl<>();
    private long lastNoticeTime;

    @Override
    public long getLastNoticeTime() {
        return lastNoticeTime;
    }

    @Override
    public void updateLastNoticeTime() {
        lastNoticeTime = System.currentTimeMillis();
    }

    @Override
    public long getLastestRecord() {
        return records.latest();
    }

    @Override
    public void addNewCall(final CallLimitConfig config) {
        records.add(System.currentTimeMillis(), config.getMaxCallNumber());
    }

    @Override
    public long getEarlyestRecord() {
        return records.earlyest();
    }

    @Override
    public boolean callable(final CallLimitConfig config) {
        return !isTooFastSoUncallable(config) && !isTooManySoUncallable(config);
    }

    // 因为在一定时间内调用太多次而不能调用
    @Override
    public boolean isTooManySoUncallable(final CallLimitConfig config) {
        return getEarlyestRecord() + config.getPeriod() > System.currentTimeMillis();
    }

    // 因为两次调用之间太快而不能调用
    @Override
    public boolean isTooFastSoUncallable(final CallLimitConfig config) {
        return System.currentTimeMillis() < getLastestRecord() + config.getCoolDown();
    }

    @Override
    @NotNull
    public Long[] list() {
        return records.list();
    }

    @Override public SizedRecorder<Long> getRecords() {
        return records;
    }

    public void setRecords(SizedRecorder<Long> records) {
        this.records = records;
    }

    public void setLastNoticeTime(long lastNoticeTime) {
        this.lastNoticeTime = lastNoticeTime;
    }
}
