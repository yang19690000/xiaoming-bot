package com.taixue.xiaomingbot.host.listener.dispatcher;

import com.taixue.xiaomingbot.api.listener.base.UserDataIsolated;
import com.taixue.xiaomingbot.api.listener.userdata.DispatcherUser;

/**
 * 状态型分派器的超类
 * @param <UserData> 需要隔离的用户数据类
 */
public abstract class Dispatcher<UserData extends DispatcherUser>
        extends UserDataIsolated<UserData> {
    public abstract void onThrowable(Throwable throwable, UserData userData);

    public abstract boolean parseCommand(UserData userData);

    public abstract void onNullProcessor(UserData userData);

    public abstract void dispatch(UserData userData) throws Exception;
}
