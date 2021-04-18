package com.taixue.xiaomingbot.api.listener.base;

import com.taixue.xiaomingbot.api.listener.userdata.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserDataIsolated<UserData extends BaseUser> {
    protected UserDataIsolator<UserData> userDataIsolator = new UserDataIsolator() {
        @Override
        public UserData getDefaultUserData() {
            return newUserData();
        }
    };
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public abstract UserData newUserData();

    public UserData getUserData(long qq) {
        return userDataIsolator.getUserData(qq);
    }

    public UserDataIsolator<UserData> getUserDataIsolator() {
        return userDataIsolator;
    }
}
