package com.taixue.xiaoming.bot.core.listener.interactor;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorMethodDetail;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InteractorMethodDetailImpl implements InteractorMethodDetail {
    private Method method;
    private String[] requiredPermissions;

    public InteractorMethodDetailImpl(@NotNull final Method method) {
        this.method = method;

        final List<String> requiredPermissions = new ArrayList<>();
        for (RequirePermission requiredPermission : method.getAnnotationsByType(RequirePermission.class)) {
            requiredPermissions.add(requiredPermission.value());
        }
        this.requiredPermissions = requiredPermissions.toArray(new String[0]);
    }

    @Override public Method getMethod() {
        return method;
    }

    @Override public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InteractorMethodDetail that = (InteractorMethodDetail) o;
        return method.equals(that.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
