package com.taixue.xiaoming.bot.api.command.executor;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Pattern;

public interface CommandFormat {
    @NotNull
    Pattern getPattern();

    @NotNull
    Set<String> getVariableNames();
}
