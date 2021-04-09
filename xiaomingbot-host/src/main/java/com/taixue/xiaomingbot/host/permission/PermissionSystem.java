package com.taixue.xiaomingbot.host.permission;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaomingbot.api.permission.BasePermissionGroup;
import com.taixue.xiaomingbot.api.permission.BasePermissionSystem;
import com.taixue.xiaomingbot.api.permission.BasePermissionUserNode;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionSystem extends BasePermissionSystem {
    protected File file;

    public static PermissionSystem forFile(File file) {
        PermissionSystem result = null;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                result = JSON.parseObject(fileInputStream, PermissionSystem.class);
            }
        }
        catch (Exception exception) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Objects.isNull(result)) {
            result = new PermissionSystem();
        }
        result.file = file;
        if (Objects.isNull(result.groups)) {
            result.groups = new HashMap<>();
        }
        if (Objects.isNull(result.users)) {
            result.users = new HashMap<>();
        }
        if (!result.groups.containsKey("default")) {
            System.err.println("权限组中必须包含名为 default 的权限组，已创建。");
            PermissionGroup permissionGroup = new PermissionGroup();
            permissionGroup.setAlias("默认权限组");
            result.addGroup("default", permissionGroup);
            result.save();
        }
        result.defaultGroup = result.getGroup("default");
        return result;
    }

    private Map<String, BasePermissionGroup> groups;
    private Map<String, BasePermissionUserNode> users;
    private transient BasePermissionGroup defaultGroup;

    public void setUsers(Map<String, BasePermissionUserNode> users) {
        this.users = users;
    }

    public void setGroups(Map<String, BasePermissionGroup> groups) {
        this.groups = groups;
    }

    public Map<String, BasePermissionUserNode> getUsers() {
        return users;
    }

    public Map<String, BasePermissionGroup> getGroups() {
        return groups;
    }

    @Override
    @Nullable
    public BasePermissionGroup getGroup(String groupName) {
        return groups.get(groupName);
    }

    @Override
    public boolean hasGroup(String groupName) {
        return getGroup(groupName) != null;
    }

    @Override
    public void addGroup(String groupName, BasePermissionGroup group) {
        groups.put(groupName, group);
        save();
    }

    @Override
    public BasePermissionUserNode getUserNode(long qq) {
        return users.get(Long.toString(qq));
    }

    @Override
    public BasePermissionUserNode getOrNewUserNode(long qq) {
        if (!hasUser(qq)) {
            PermissionUserNode node = new PermissionUserNode();
            node.setPermissions(new ArrayList<>());
            node.setGroup("default");
            users.put(qq + "", node);
        }
        return getUserNode(qq);
    }

    @Override
    public void giveUserPermission(long who, String node) {
        getOrNewUserNode(who).addPermission(node);
        save();
    }

    @Override
    public void giveGroupPermission(String groupName, String node) {
        BasePermissionGroup group = getGroup(groupName);
        group.addPermission(node);
        save();
    }

    @Override
    public boolean hasUser(long qq) {
        return getUserNode(qq) != null;
    }

    @Override
    public BasePermissionGroup getUserGroup(long qq) {
        if (hasUser(qq)) {
            String group = getUserNode(qq).getGroup();
            if (Objects.nonNull(group)) {
                return getGroup(group);
            }
            return getDefaultGroup();
        }
        else {
            return getDefaultGroup();
        }
    }

    @Override
    public void setUserGroup(long qq, String groupName) {
        if ("default".equals(groupName)) {
            users.remove(Long.toString(qq));
        }
        else {
            getOrNewUserNode(qq).setGroup(groupName);
        }
    }

    @Override
    public BasePermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    @Override
    public boolean hasPermission(BasePermissionGroup group, String node) {
        return false;
    }

    public boolean hasPermission(PermissionGroup group, String node) {
        for (String n: group.permissions) {
            if (isPermission(n, node)) {
                return true;
            }
        }
        for (String superGroupName: group.superGroups) {
            BasePermissionGroup superGroup = getGroup(superGroupName);
            if (Objects.isNull(superGroup)) {
                System.err.println("找不到权限组：" + superGroupName);
            }
            else if (hasPermission(superGroup, node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(long qq, String node) {
        BasePermissionUserNode permissionUserNode = getUserNode(qq);
        if (Objects.isNull(permissionUserNode)) {
            return hasPermission(getDefaultGroup(), node);
        }
        return permissionUserNode.hasPrivatePermission(node) || hasPermission(permissionUserNode.getGroup(), node);
    }

    @Override
    public boolean hasPermission(String groupName, String node) {
        BasePermissionGroup group = getGroup(groupName);
        if (Objects.isNull(group)) {
            return false;
        }
        else {
            return hasPermission(group, node);
        }
    }

    public void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String jsonString = JSON.toJSONString(this);
            fileOutputStream.write(jsonString.getBytes());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}