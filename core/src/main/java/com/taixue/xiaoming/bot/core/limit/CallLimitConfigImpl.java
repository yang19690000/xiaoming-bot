package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class CallLimitConfigImpl implements CallLimitConfig {
    private long coolDown = TimeUtil.SECOND_MINS * 10;
    private long period = TimeUtil.HOUR_MINS * 12;
    private int top = 10;
    private long deltaNoticeTime = period;

    @Override
    public long getCoolDown() {
        return coolDown;
    }

    @Override
    public void setCoolDown(long coolDown) {
        this.coolDown = coolDown;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public void setTop(int top) {
        this.top = top;
    }

    @Override
    public void setDeltaNoticeTime(long deltaNoticeTime) {
        this.deltaNoticeTime = deltaNoticeTime;
    }

    @Override
    public long getDeltaNoticeTime() {
        return deltaNoticeTime;
    }
}
