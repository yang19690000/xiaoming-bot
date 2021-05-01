package com.taixue.xiaoming.bot.core.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class ConsoleDispatcherUserImpl extends DispatcherUserImpl implements ConsoleDispatcherUser {
    private String message;
    private long qq;
    private long group;

    @Override
    public void setMessage(String message) {
        this.message = message;
        getRecentInputs().add(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return getMsgSender().GETTER.getFriendInfo(getQQ());
    }

    @Override
    public MsgSender getMsgSender() {
        return getXiaomingBot().getMsgSender();
    }

    @Override
    public long getQQ() {
        return qq;
    }

    @Override
    public GroupInfo getGroupInfo() {
        return getMsgSender().GETTER.getGroupInfo(getGroup());
    }

    @Override
    public long getGroup() {
        return group;
    }

    @Override
    public void setQQ(long qq) {
        this.qq = qq;
    }

    @Override
    public void setGroup(long group) {
        this.group = group;
    }

    @Override
    public String getQQString() {
        return String.valueOf(getQQ());
    }

    @Override
    public boolean sendGroupMessage(String message, Object... arguments) {
        getLogger().info("(群消息) [" + getGroupInfo().getGroupName() + "（" + getGroupString() + "）" + "] " + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public boolean sendPrivateMessage(String message, Object... arguments) {
        getLogger().info("(私聊消息) " + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public String getCompleteName() {
        return "[[控制台]] " + super.getCompleteName();
    }
}