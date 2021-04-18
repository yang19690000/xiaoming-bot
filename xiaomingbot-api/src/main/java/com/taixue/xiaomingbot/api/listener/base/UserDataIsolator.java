package com.taixue.xiaomingbot.api.listener.base;

import com.taixue.xiaomingbot.api.listener.userdata.BaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态型调度器和分派器用于隔离用户数据的工具
 * @param <UserData> 需要隔离的用户数据类
 */
public abstract class UserDataIsolator<UserData extends BaseUser> {
    private Map<Long, UserData> value = new HashMap<>();

    public UserData registerUserData(long qq) {
        UserData userData = getDefaultUserData();
        value.put(qq, userData);
        return userData;
    }

    public UserData getUserData(long qq) {
        if (!hasUserData(qq)) {
            registerUserData(qq);
        }
        return value.get(qq);
    }

    public Map<Long, UserData> getValue() {
        return value;
    }

    public void setValue(Map<Long, UserData> value) {
        this.value = value;
    }

    public boolean hasUserData(long qq) {
        return value.containsKey(qq);
    }

    public void removeUserData(long qq) {
        value.remove(qq);
    }

    public abstract UserData getDefaultUserData();
}
