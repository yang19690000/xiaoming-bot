package com.taixue.xiaoming.bot.core.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class ConsoleDispatcherUserImpl extends DispatcherUserImpl implements ConsoleDispatcherUser {
    private String message;
    private long qq;
    private long group;

    @Override public void setMessage(String message) {
        this.message = message;
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
    public String getName() {
        return "CONSOLE";
    }

    @Override
    protected void sendMessage(final String message) {
        getLogger().info(message);
    }

    @Override
    public void sendError(String message, Object... arguments) {
        getLogger().error(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendWarning(String message, Object... arguments) {
        getLogger().warn(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public MsgSender getMsgSender() {
        return null;
    }

    @Override
    public long getQQ() {
        return qq;
    }

    @Override
    public GroupInfo getGroupInfo() {
        return getMsgSender().GETTER.getGroupInfo(getGroup());
    }

    @Override public long getGroup() {
        return group;
    }

    @Override public void setQQ(long qq) {
        this.qq = qq;
    }

    @Override public void setGroup(long group) {
        this.group = group;
    }

    @Override
    public String getQQString() {
        return String.valueOf(getQQ());
    }
}
