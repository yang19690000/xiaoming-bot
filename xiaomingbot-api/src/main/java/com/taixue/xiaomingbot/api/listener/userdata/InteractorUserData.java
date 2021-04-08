package com.taixue.xiaomingbot.api.listener.userdata;

import java.util.List;

/**
 * 交互器的用户数据类型
 */
public class InteractorUserData extends StatedUserData {
    protected boolean shouldExit;

    public InteractorUserData() {}

    public InteractorUserData(StatedUserData userData) {
        setQQ(userData.getQQ());
        setMessage(userData.getMessage());
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    public boolean isShouldExit() {
        return shouldExit;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }
}