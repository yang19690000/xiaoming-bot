package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.user.ConsoleXiaomingUser;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.util.ArgumentUtil;

public interface ConsoleDispatcherUser extends DispatcherUser, GroupXiaomingUser, ConsoleXiaomingUser {
    void setMessage(String message);

    @Override
    String getMessage();

    @Override
    long getQQ();

    long getGroup();

    void setQQ(long qq);

    void setGroup(long group);

    @Override
    default void sendWarning(String message, Object... arguments) {
        getLogger().warn(message, arguments);
    }

    @Override
    default void sendError(String message, Object... arguments) {
        getLogger().error(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    default void sendMessage(String message, Object... arguments) {
        getLogger().info(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    default void sendNoArgumentMessage(String message) {
        getLogger().info(message);
    }
}
