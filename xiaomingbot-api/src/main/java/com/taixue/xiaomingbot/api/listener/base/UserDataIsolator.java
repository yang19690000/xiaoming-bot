package com.taixue.xiaomingbot.api.listener.base;

import com.taixue.xiaomingbot.api.listener.userdata.InteractorUserData;
import com.taixue.xiaomingbot.api.listener.userdata.StatedUserData;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态型调度器和分派器用于隔离用户数据的工具
 * @param <UserData> 需要隔离的用户数据类
 */
public abstract class UserDataIsolator<UserData extends StatedUserData> {
    protected Map<Long, UserData> userDataMap = new HashMap<>();

    public void registerUserData(long qq) {
        UserData defaultUserData = getDefaultUserData();
        defaultUserData.setQQ(qq);
        userDataMap.put(qq, defaultUserData);
        defaultUserData.toState(InteractorUserData.DEFAULT_STATE);
    }

    public UserData getUserData(long qq) {
        if (!hasUserData(qq)) {
            registerUserData(qq);
        }
        return userDataMap.get(qq);
    }

    public boolean hasUserData(long qq) {
        return userDataMap.containsKey(qq);
    }

    public void removeUserData(long qq) {
        userDataMap.remove(qq);
    }

    public abstract UserData getDefaultUserData();
}
