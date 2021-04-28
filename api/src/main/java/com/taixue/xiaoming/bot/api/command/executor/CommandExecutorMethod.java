package com.taixue.xiaoming.bot.api.command.executor;

import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;

import java.lang.reflect.Method;

public interface CommandExecutorMethod {
    String[] getUsages();

    CommandFormat[] getFormats();

    Method getMethod();

    String[] getRequiredPermission();
}
