package com.taixue.xiaoming.bot.api.permission;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;

import java.util.List;

public interface PermissionGroup extends XiaomingObject {
    void setAlias(String alias);

    List<String> getSuperGroups();

    void setSuperGroups(List<String> superGroups);

    void setPermissions(List<String> permissions);

    List<String> getPermissions();

    void addPermission(String node);

    String getAlias();

    void removePermission(String node);
}
