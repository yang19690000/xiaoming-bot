package com.taixue.xiaomingbot.api.command;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequiredPermissions.class)
public @interface RequiredPermission {
    String value();
}
