package com.taixue.xiaomingbot.api.permission;

import org.jetbrains.annotations.Nullable;

public abstract class BasePermissionSystem {
    @Nullable
    public abstract BasePermissionGroup getGroup(String groupName);

    public abstract boolean hasGroup(String groupName);

    public abstract void addGroup(String groupName, BasePermissionGroup group);

    public abstract BasePermissionUserNode getUserNode(long qq);

    public abstract BasePermissionUserNode getOrNewUserNode(long qq);

    public abstract void giveUserPermission(long who, String node);

    public abstract void giveGroupPermission(String groupName, String node);

    public abstract boolean hasUser(long qq);

    public abstract BasePermissionGroup getUserGroup(long qq);

    public abstract void setUserGroup(long qq, String groupName);

    public abstract BasePermissionGroup getDefaultGroup();

    public static boolean isPermission(String node, String give) {
        if (node.equals(give)) {
            return true;
        }
        if (node.startsWith("-")) {
            return false;
        }
        if (node.endsWith("*") && give.startsWith(node.substring(0, node.lastIndexOf("*")))) {
            return true;
        }
        return false;
    }

    public abstract boolean hasPermission(BasePermissionGroup group, String node);

    public abstract boolean hasPermission(long qq, String node);

    public abstract boolean hasPermission(String groupName, String node);
}