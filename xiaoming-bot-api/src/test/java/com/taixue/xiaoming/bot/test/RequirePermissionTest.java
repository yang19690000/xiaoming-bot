package com.taixue.xiaoming.bot.test;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.annotation.RequirePermissions;

import java.lang.reflect.Method;

public class RequirePermissionTest {
    @RequiredPermission("test")
    public void method() {

    }

    public static void main(String[] args) {
        final Class<RequirePermissionTest> clazz = RequirePermissionTest.class;
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequirePermissions.class)) {
                System.out.println("method " + method + " is presented by " + RequirePermissions.class);
            }
        }
    }
}
