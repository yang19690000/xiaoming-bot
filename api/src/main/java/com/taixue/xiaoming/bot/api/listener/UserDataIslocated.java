package com.taixue.xiaoming.bot.api.listener;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface UserDataIslocated<Key, Value> {
    Value getUser(Key key);

    void putUser(Key key, Value value);

    void removeUser(Key key);
}