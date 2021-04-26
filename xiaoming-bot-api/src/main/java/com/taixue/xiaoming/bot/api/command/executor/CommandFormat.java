package com.taixue.xiaoming.bot.api.command.executor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandFormats.class)
public @interface CommandFormat {
    String value();
}
