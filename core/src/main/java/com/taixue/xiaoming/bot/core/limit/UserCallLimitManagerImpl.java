package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.api.limit.UserCallLimiter;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

public class UserCallLimitManagerImpl extends JsonFileSavedData implements UserCallLimitManager {
    private UserCallLimiter groupCallLimiter = new UserCallLimiterImpl();
    private UserCallLimiter privateCallLimiter = new UserCallLimiterImpl();

    @Override
    public UserCallLimiter getGroupCallLimiter() {
        return groupCallLimiter;
    }

    public void setGroupCallLimiter(UserCallLimiter groupCallLimiter) {
        this.groupCallLimiter = groupCallLimiter;
    }

    @Override
    public UserCallLimiter getPrivateCallLimiter() {
        return privateCallLimiter;
    }

    public void setPrivateCallLimiter(UserCallLimiter privateCallLimiter) {
        this.privateCallLimiter = privateCallLimiter;
    }
}
