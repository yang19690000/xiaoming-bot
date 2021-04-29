package com.taixue.xiaoming.bot.api.user;

public interface PrivateXiaomingUser extends QQXiaomingUser {
    @Override
    default void sendNoArgumentMessage(String message) {
        sendPrivateMessage(message);
    }
}
