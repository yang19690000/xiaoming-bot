package com.taixue.xiaomingbot.api.timetask;

import com.taixue.xiaomingbot.util.JSONFileData;

import java.util.List;

public class TimeTaskManager extends JSONFileData {
    private List<TimeTask> timeTasks;

    public List<TimeTask> getTimeTasks() {
        return timeTasks;
    }

    public void setTimeTasks(List<TimeTask> timeTasks) {
        this.timeTasks = timeTasks;
    }
}
