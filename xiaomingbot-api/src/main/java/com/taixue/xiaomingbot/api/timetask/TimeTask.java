package com.taixue.xiaomingbot.api.timetask;

public abstract class TimeTask {
    private long frequency;
    private long createTime;
    private long beginTime;
    private long lastActionTime;

    public long getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public void updateLastActionTime() {
        setLastActionTime(System.currentTimeMillis());
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public abstract void execute();
}
