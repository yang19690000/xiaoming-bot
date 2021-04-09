package com.taixue.xiaomingbot.api.command;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandFormats.class)
public @interface CommandFormat {
    String value();
}
