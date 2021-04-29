package com.taixue.xiaoming.bot.core.user;

import catcode.CatCodeUtil;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import net.mamoe.mirai.contact.BotIsBeingMutedException;

import java.io.IOException;
import java.util.NoSuchElementException;

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
