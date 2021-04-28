package com.taixue.xiaoming.bot.core.permission;

import com.taixue.xiaoming.bot.api.permission.PermissionUserNode;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUserNodeImpl extends HostObjectImpl implements PermissionUserNode {
    private String group;
    private List<String> permissions = new ArrayList<>();

    @Override
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void addPermission(String node) {
        permissions.add(node);
    }

    @Override
    public boolean hasPrivatePermission(String node) {
        if (Objects.isNull(permissions)) {
            return false;
        }
        for (String per : permissions) {
            final int accessable = PermissionUtil.accessable(per, node);
            if (accessable == 0) {
                continue;
            } else {
                return accessable > 0;
            }
        }
        return false;
    }

    @Override
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
