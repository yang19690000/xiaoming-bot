package com.taixue.xiaoming.bot.api.permission;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;

import java.util.List;

public interface PermissionUserNode extends XiaomingObject {
    void setPermissions(List<String> permissions);

    List<String> getPermissions();

    String getGroup();

    void setGroup(String group);

    void addPermission(String node);

    boolean hasPrivatePermission(String node);

    boolean hasPermission(String node);
}
