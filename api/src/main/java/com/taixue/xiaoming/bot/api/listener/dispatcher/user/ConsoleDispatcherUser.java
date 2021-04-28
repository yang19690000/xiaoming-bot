package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.user.ConsoleXiaomingUser;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;

public interface ConsoleDispatcherUser extends DispatcherUser, GroupXiaomingUser, ConsoleXiaomingUser {
    void setMessage(String message);

    @Override
    String getMessage();

    @Override
    long getQQ();

    long getGroup();

    void setQQ(long qq);

    void setGroup(long group);
}
