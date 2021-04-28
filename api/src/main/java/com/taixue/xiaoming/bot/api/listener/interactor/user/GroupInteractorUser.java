package com.taixue.xiaoming.bot.api.listener.interactor.user;

import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import love.forte.simbot.api.message.events.GroupMsg;

public interface GroupInteractorUser extends InteractorUser, GroupXiaomingUser {
    void setGroupMsg(GroupMsg groupMsg);

    GroupMsg getGroupMsg();
}
