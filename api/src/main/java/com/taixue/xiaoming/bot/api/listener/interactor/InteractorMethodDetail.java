package com.taixue.xiaoming.bot.api.listener.interactor;

import java.lang.reflect.Method;

public interface InteractorMethodDetail {
    Method getMethod();

    String[] getRequiredPermissions();
}
