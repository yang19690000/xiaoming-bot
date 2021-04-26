package com.taixue.xiaoming.bot.api.permission;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUserNode extends HostObject {
    private String group;
    private List<String> permissions = new ArrayList<>();

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
        permissions.add(node);
    }

    public boolean hasPrivatePermission(String node) {
        if (Objects.isNull(permissions)) {
            return false;
        }
        for (String per: permissions) {
            final int accessable = PermissionUtil.accessable(per, node);
            if (accessable == 0) {
                continue;
            }
            else {
                return accessable > 0;
            }
        }
        return false;
    }

    public boolean hasPermission(String node) {
        // 先检查私有权限
        if (Objects.nonNull(permissions)) {
            for (String per : permissions) {
                final int accessable = PermissionUtil.accessable(per, node);
                if (accessable == 0) {
                    continue;
                } else {
                    return accessable > 0;
                }
            }
        }
        return Objects.nonNull(group) && getXiaomingBot().getPermissionManager().groupHasPermission(group, node);
    }
}
