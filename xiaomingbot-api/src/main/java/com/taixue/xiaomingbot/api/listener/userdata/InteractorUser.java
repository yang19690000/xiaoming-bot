package com.taixue.xiaomingbot.api.listener.userdata;

/**
 * 交互器的用户数据类型
 */
public abstract class InteractorUser extends BaseUser {
    private boolean shouldExit;

    public InteractorUser() {}

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    public boolean isShouldExit() {
        return shouldExit;
    }
}