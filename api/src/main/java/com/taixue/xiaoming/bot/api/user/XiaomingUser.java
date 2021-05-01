package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.base.HostObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface XiaomingUser extends HostObject {
    boolean sendMessage(String message,
                     Object... arguments);

    boolean sendError(String message,
                   Object... arguments);

    boolean sendWarning(String message,
                     Object... arguments);

    boolean hasPermission(String node);

    boolean hasPermissions(@NotNull String[] nodes);

    String getName();

    List<String> getRecentInputs();

    void setRecentInputs(List<String> inputs);

    default String getCompleteName() {
        return getName();
    }

    default boolean checkPermissionAndReport(String node) {
        if (hasPermission(node)) {
            return true;
        } else {
            lackPermission(node);
            return false;
        }
    }

    default boolean checkPermissionsAndReport(String[] nodes) {
        for (String node : nodes) {
            if (!checkPermissionAndReport(node)) {
                return false;
            }
        }
        return true;
    }

    default void lackPermission(String node) {
        sendError("小明不能帮忙做这件事哦，因为你还没有权限：{}", node);
    }
}
