package com.taixue.xiaoming.bot.core.listener.interactor.user;

import com.taixue.xiaoming.bot.api.listener.interactor.user.GroupInteractorUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

public class GroupInteractorUserImpl extends InteractorUserImpl implements GroupInteractorUser {
    private GroupMsg groupMsg;

    @Override
    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
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

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public GroupMsg getGroupMsg() {
        return groupMsg;
    }
}