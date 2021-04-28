package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import love.forte.simbot.api.message.events.GroupMsg;

public interface GroupDispatcherUser extends DispatcherUser, GroupXiaomingUser {
    void setGroupMsg(GroupMsg groupMsg);

    GroupMsg getGroupMsg();
}
