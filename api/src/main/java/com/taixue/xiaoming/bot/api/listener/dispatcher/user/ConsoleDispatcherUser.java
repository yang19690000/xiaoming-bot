package com.taixue.xiaoming.bot.api.listener.dispatcher.user;

import com.taixue.xiaoming.bot.api.user.ConsoleXiaomingUser;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.util.ArgumentUtil;

public interface ConsoleDispatcherUser extends DispatcherUser, GroupXiaomingUser, ConsoleXiaomingUser {
    void setMessage(String message);

    @Override
    default String getName() {
        return "CONSOLE";
    }

    @Override
    String getMessage();

    @Override
    long getQQ();

    @Override
    long getGroup();

    void setQQ(long qq);

    void setGroup(long group);

    @Override
    default boolean sendWarning(String message, Object... arguments) {
        getLogger().warn(message, arguments);
        return true;
    }

    @Override
    default boolean sendError(String message, Object... arguments) {
        getLogger().error(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendMessage(String message, Object... arguments) {
        getLogger().info(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }
}
