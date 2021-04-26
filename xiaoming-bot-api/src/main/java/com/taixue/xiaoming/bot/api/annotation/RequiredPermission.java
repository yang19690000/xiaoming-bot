package com.taixue.xiaoming.bot.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequirePermissions.class)
public @interface RequiredPermission {
    String value();
}
