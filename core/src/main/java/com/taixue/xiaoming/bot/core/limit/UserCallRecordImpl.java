package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallRecord;
import org.jetbrains.annotations.NotNull;

public class UserCallRecordImpl implements UserCallRecord {
    private UserCallSizedRecord recentCalls = new UserCallSizedRecord();
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
        return recentCalls.latest();
    }

    @Override
    public void addNewCall(final CallLimitConfig config) {
        recentCalls.add(System.currentTimeMillis(), config.getTop());
    }

    @Override
    public long getEarlyestRecord() {
        return recentCalls.earlyest();
    }

    @Override
    public boolean callable(final CallLimitConfig config) {
        return !isTooFastSoUncallable(config) && !isTooManySoUncallable(config);
    }

    // 因为在一定时间内调用太多次而不能调用
    @Override
    public boolean isTooManySoUncallable(final CallLimitConfig config) {
        return recentCalls.size() == config.getTop() && getEarlyestRecord() + config.getPeriod() > System.currentTimeMillis();
    }

    // 因为两次调用之间太快而不能调用
    @Override
    public boolean isTooFastSoUncallable(final CallLimitConfig config) {
        return !recentCalls.empty() && System.currentTimeMillis() < getLastestRecord() + config.getCoolDown();
    }

    @Override
    @NotNull
    public Long[] list() {
        return recentCalls.list();
    }

    @Override
    public UserCallSizedRecord getRecentCalls() {
        return recentCalls;
    }
}
