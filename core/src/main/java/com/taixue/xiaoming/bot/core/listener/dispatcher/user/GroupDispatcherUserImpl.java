package com.taixue.xiaoming.bot.core.listener.dispatcher.user;

import catcode.CatCodeUtil;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public class GroupDispatcherUserImpl extends DispatcherUserImpl implements GroupDispatcherUser {
    private GroupMsg groupMsg;

    @Override
    public AccountInfo getAccountInfo() {
        return groupMsg.getAccountInfo();
    }

    @Override
    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
    }

    @Override
    public String getMessage() {
        return groupMsg.getMsg();
    }

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public GroupInfo getGroupInfo() {
        return groupMsg.getGroupInfo();
    }

    @Override
    public GroupMsg getGroupMsg() {
        return groupMsg;
    }

    @Override
    public String getCompleteName() {
        return "[ " + getGroupInfo().getGroupName() + "（" + getGroupString() + "）" + "] " + super.getCompleteName();
    }
}