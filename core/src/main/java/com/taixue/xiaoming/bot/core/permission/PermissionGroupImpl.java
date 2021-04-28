package com.taixue.xiaoming.bot.core.permission;

import com.taixue.xiaoming.bot.api.permission.PermissionGroup;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroupImpl extends HostObjectImpl implements PermissionGroup {
    private List<String> superGroups = new ArrayList<>();
    private String alias;
    private List<String> permissions = new ArrayList<>();

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public List<String> getSuperGroups() {
        return superGroups;
    }

    @Override
    public void setSuperGroups(List<String> superGroups) {
        this.superGroups = superGroups;
    }

    @Override
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public void addPermission(String node) {
        permissions.add(node);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void removePermission(String node) {
        permissions.remove(node);
    }
}
