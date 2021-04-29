package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;

/**
 * 群聊中的小明调度器使用者
 * @author Chuanwise
 */
public class GroupXiaomingUserImpl extends QQXiaomingUserImpl implements GroupXiaomingUser {
    private GroupMsg groupMsg;

    public GroupMsg getGroupMsg() {
        return groupMsg;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return groupMsg.getAccountInfo();
    }

    @Override
    public String getMessage() {
        return groupMsg.getMsg();
    }

    @Override
    public GroupInfo getGroupInfo() {
        return groupMsg.getGroupInfo();
    }
}
