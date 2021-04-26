package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.util.ArgumentUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class ConsoleDispatcherUser extends DispatcherUser {
    private String message;
    private long qq;
    private long group;
    @Depend
    private MsgSender msgSender;

    public void setMessage(String message) {
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
        return msgSender;
    }

    @Override
    public long getQQ() {
        return qq;
    }

    public long getGroup() {
        return group;
    }

    public void setQQ(long qq) {
        this.qq = qq;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    @Override
    public String getQQString() {
        return getQQ() + "";
    }
}
