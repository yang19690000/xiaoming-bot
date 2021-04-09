package com.taixue.xiaomingbot.api.permission;

import com.taixue.xiaomingbot.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUserNode {
    protected String group;
    protected List<String> permissions;

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void addPermission(String node) {
        if (Objects.isNull(permissions)) {
            permissions = new ArrayList<>();
        }
        permissions.add(node);
    }

    public boolean hasPrivatePermission(String node) {
        if (Objects.isNull(permissions)) {
            return false;
        }
        for (String per: permissions) {
            if (per.equals("-" + node)) {
                return false;
            }
            if (PermissionUtil.accessable(per, node)) {
                return true;
            }
        }
        return false;
    }
}
