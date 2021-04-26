package com.taixue.xiaoming.bot.api.permission;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.util.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PermissionManager extends JsonFileSavedData {
    public static final String DEFAULT_PERMISSION_GROUP_NAME = "default";
    private Map<String, PermissionGroup> groups = new HashMap<>();
    private transient PermissionGroup defaultGroup;

    public void setGroups(Map<String, PermissionGroup> groups) {
        this.groups = groups;
        PermissionGroup defaultGroup = groups.get(DEFAULT_PERMISSION_GROUP_NAME);
        if (Objects.nonNull(defaultGroup)) {
            this.defaultGroup = defaultGroup;
        } else {
            getLogger().error("权限组至少应该有一个默认权限组 default");
            defaultGroup = new PermissionGroup();
            defaultGroup.setAlias("默认组");
            this.defaultGroup = defaultGroup;
            addGroup(DEFAULT_PERMISSION_GROUP_NAME, defaultGroup);
        }
    }

    public boolean userHasPermission(final long qq,
                                     final String node) {
        final PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            return groupHasPermission(defaultGroup, node);
        } else {
            try {
                return userNode.hasPermission(node);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
    }

    public boolean userHasPermissions(final long qq,
                                      @NotNull final String[] nodes) {
        for (String node : nodes) {
            if (!userHasPermission(qq, node)) {
                return false;
            }
        }
        return true;
    }

    public boolean groupHasPermission(final String groupName, final String node) {
        final PermissionGroup group = getGroup(groupName);
        return Objects.nonNull(group) && groupHasPermission(group, node);
    }

    @Nullable
    public PermissionGroup getGroup(final String groupName) {
        return groups.get(groupName);
    }

    public boolean groupHasPermission(final PermissionGroup group,
                                      final String node) {
        for (String n: group.getPermissions()) {
            final int accessable = PermissionUtil.accessable(n, node);
            if (accessable == 0) {
                continue;
            } else {
                return accessable > 0;
            }
        }
        for (String superGroupName: group.getSuperGroups()) {
            PermissionGroup superGroup = getGroup(superGroupName);
            if (Objects.isNull(superGroup)) {
                return false;
            }
            else if (groupHasPermission(superGroup, node)) {
                return true;
            }
        }
        return false;
    }

    public void addGroup(String groupName, PermissionGroup group) {
        if (Objects.equals(groupName, DEFAULT_PERMISSION_GROUP_NAME) &&
                Objects.nonNull(defaultGroup)) {
            getLogger().warn("增加的权限组，将覆盖此前的默认权限组");
            this.defaultGroup = group;
        }
        groups.put(groupName, group);
    }

    public void removeGroup(String groupName) {
        if (Objects.equals(groupName, DEFAULT_PERMISSION_GROUP_NAME)) {
            getLogger().warn("默认权限组不能被移除");
        } else {
            groups.remove(groupName);
        }
    }

    public Map<String, PermissionGroup> getGroups() {
        return groups;
    }

    public boolean removeUserPermission(long qq, String node) {
        if (userHasPermission(qq, node)) {
            PermissionUserNode userNode = null;
            final Account account = XiaomingBot.getInstance().getAccountManager().getOrPutAccount(qq);

            if (Objects.isNull(userNode)) {
                userNode = new PermissionUserNode();
                userNode.setGroup(DEFAULT_PERMISSION_GROUP_NAME);
                account.putProperty("permission", userNode);
            }
            userNode.getPermissions().remove(node);
            if (userHasPermission(qq, node)) {
                List<String> permissions = new ArrayList<>();
                permissions.add('-' + node);
                if (Objects.nonNull(userNode.getPermissions())) {
                    permissions.addAll(userNode.getPermissions());
                }
                userNode.setPermissions(permissions);
            }
            return save();
        }
        else {
            return false;
        }
    }

    @Nullable
    public PermissionUserNode getUserNode(final long qq) {
        final Account account = XiaomingBot.getInstance().getAccountManager().getAccount(qq);
        return Objects.nonNull(account) ? getUserNode(account) : null;
    }

    @Nullable
    public PermissionUserNode getUserNode(@NotNull final Account account) {
        return account.getProperty("permission", PermissionUserNode.class);
    }

    @NotNull
    public PermissionUserNode getOrPutUserNode(final long qq) {
        final Account account = XiaomingBot.getInstance().getAccountManager().getOrPutAccount(qq);
        return getOrPutUserNode(account);
    }

    @NotNull
    public PermissionUserNode getOrPutUserNode(final Account account) {
        PermissionUserNode permission = getUserNode(account);
        if (Objects.isNull(permission)) {
            permission = new PermissionUserNode();
            permission.setGroup(DEFAULT_PERMISSION_GROUP_NAME);
            account.putProperty("permission", permission);
        }
        save();
        return permission;
    }
}
