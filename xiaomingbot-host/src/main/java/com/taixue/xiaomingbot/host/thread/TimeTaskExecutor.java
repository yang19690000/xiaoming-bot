package com.taixue.xiaomingbot.host.thread;

import com.taixue.xiaomingbot.api.timetask.TimeTask;
import com.taixue.xiaomingbot.api.timetask.TimeTaskManager;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.timer.EnableTimeTask;
import love.forte.simbot.timer.Fixed;

import java.util.List;
import java.util.concurrent.TimeUnit;

// @Beans
// @EnableTimeTask
public class TimeTaskExecutor {
//    public static final long TIME_TASK_TIME = DateUtil.MINUTE_MINS * 10;
    public static final long TIME_TASK_TIME = DateUtil.MINUTE_MINS;

    // @Fixed(value = TIME_TASK_TIME)
    public void run() {
        // 下次运行该函数的时间
        final long nextRunTime = System.currentTimeMillis() + TIME_TASK_TIME;
        final TimeTaskManager manager = XiaomingBot.getInstance().getTimeTaskManager();
        final List<TimeTask> timeTasks = manager.getTimeTasks();
        for (TimeTask timeTask : timeTasks) {
            // 该开始执行的时候：没执行过或即将执行
            if (timeTask.getLastActionTime() == 0 || timeTask.getFrequency() != 0) {
                long nextExecutorTime;
                if (timeTask.getFrequency() == 0) {
                    nextExecutorTime = timeTask.getBeginTime();
                }
                else {
                    if (timeTask.getLastActionTime() == 0) {
                        nextExecutorTime = timeTask.getBeginTime();
                    }
                    else {
                        nextExecutorTime = timeTask.getLastActionTime() + timeTask.getFrequency();
                    }
                }
            }
        }
    }
}
