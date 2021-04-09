package com.taixue.xiaomingbot.host.permission;

import com.taixue.xiaomingbot.api.permission.BasePermissionGroup;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroup extends BasePermissionGroup {
    protected List<String> superGroups = new ArrayList<>();
    protected String alias;
    protected List<String> permissions = new ArrayList<>();

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getSuperGroups() {
        return superGroups;
    }

    public void setSuperGroups(List<String> superGroups) {
        this.superGroups = superGroups;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String node) {
        permissions.add(node);
    }

    public String getAlias() {
        return alias;
    }
}
