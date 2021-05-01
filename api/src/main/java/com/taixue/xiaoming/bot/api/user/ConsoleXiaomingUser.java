package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.util.ArgumentUtil;

public interface ConsoleXiaomingUser extends XiaomingUser {
    @Override
    boolean hasPermission(String node);

    @Override
    String getName();

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

    @Override
    default boolean sendWarning(String message, Object... arguments) {
        getLogger().warn(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }
}
