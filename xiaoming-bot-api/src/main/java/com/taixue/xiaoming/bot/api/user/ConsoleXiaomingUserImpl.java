package com.taixue.xiaoming.bot.api.user;

/**
 * @author Chuanwise
 */
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl {
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
