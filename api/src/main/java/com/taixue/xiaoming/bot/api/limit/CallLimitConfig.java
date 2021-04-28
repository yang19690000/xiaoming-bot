package com.taixue.xiaoming.bot.api.limit;

public interface CallLimitConfig {
    long getCoolDown();

    void setCoolDown(long coolDown);

    long getPeriod();

    void setPeriod(long period);

    int getMaxCallNumber();

    void setMaxCallNumber(int maxCallNumber);

    void setDeltaNoticeTime(long time);

    long getDeltaNoticeTime();
}
