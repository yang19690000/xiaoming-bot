package com.taixue.xiaoming.bot.api.user;

public interface ConsoleXiaomingUser extends XiaomingUser {
    @Override
    boolean hasPermission(String node);

    @Override
    String getName();
}
