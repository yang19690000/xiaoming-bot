package com.taixue.xiaoming.bot.api.user;

public interface PrivateXiaomingUser extends QQXiaomingUser {
    @Override
    default boolean sendMessage(String message, Object... arguments) {
        return sendPrivateMessage(message, arguments);
    }
}
