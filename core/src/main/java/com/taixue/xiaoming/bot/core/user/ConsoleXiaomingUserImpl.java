package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.ConsoleXiaomingUser;

/**
 * @author Chuanwise
 */
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl implements ConsoleXiaomingUser {
    @Override
    protected void sendMessage(String message) {
        getLogger().info(message);
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }
}