package com.taixue.xiaomingbot.host.thread;

import com.taixue.xiaomingbot.api.timetask.TimeTask;

public class TimeTaskRunner implements Runnable {
    private final TimeTask timeTask;
    private final long sleepTime;

    public TimeTaskRunner(TimeTask timeTask, long sleepTime) {
        this.timeTask = timeTask;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleepTime);
            // timeTask.
        } catch (InterruptedException e) {
        }
    }
}
