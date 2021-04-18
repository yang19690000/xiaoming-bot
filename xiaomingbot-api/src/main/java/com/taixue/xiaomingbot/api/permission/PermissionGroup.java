package com.taixue.xiaomingbot.api.permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroup {
    private List<String> superGroups = new ArrayList<>();
    private String alias;
    private List<String> permissions = new ArrayList<>();

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

    public void removePermission(String node) {
        permissions.remove(node);
    }
}
