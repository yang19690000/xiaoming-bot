package com.taixue.xiaomingbot.api.permission;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaomingbot.util.PermissionUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Chuanwise
 */
public class PermissionSystem {
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

    private Map<String, PermissionGroup> groups;
    private Map<String, PermissionUserNode> users;
    private transient PermissionGroup defaultGroup;

    public void setUsers(Map<String, PermissionUserNode> users) {
        this.users = users;
    }

    public void setGroups(Map<String, PermissionGroup> groups) {
        this.groups = groups;
    }

    public Map<String, PermissionUserNode> getUsers() {
        return users;
    }

    public Map<String, PermissionGroup> getGroups() {
        return groups;
    }

    @Nullable
    public PermissionGroup getGroup(String groupName) {
        return groups.get(groupName);
    }

    public boolean hasGroup(String groupName) {
        return getGroup(groupName) != null;
    }

    public void addGroup(String groupName, PermissionGroup group) {
        groups.put(groupName, group);
        save();
    }

    public PermissionUserNode getUserNode(long qq) {
        return users.get(Long.toString(qq));
    }

    public PermissionUserNode getOrNewUserNode(long qq) {
        if (!hasUser(qq)) {
            PermissionUserNode node = new PermissionUserNode();
            node.setPermissions(new ArrayList<>());
            node.setGroup("default");
            users.put(qq + "", node);
        }
        return getUserNode(qq);
    }

    public void giveUserPermission(long who, String node) {
        getOrNewUserNode(who).addPermission(node);
        save();
    }

    public void giveGroupPermission(String groupName, String node) {
        PermissionGroup group = getGroup(groupName);
        group.addPermission(node);
        save();
    }

    public boolean hasUser(long qq) {
        return getUserNode(qq) != null;
    }

    public PermissionGroup getUserGroup(long qq) {
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

    public void setUserGroup(long qq, String groupName) {
        if ("default".equals(groupName)) {
            users.remove(Long.toString(qq));
        }
        else {
            getOrNewUserNode(qq).setGroup(groupName);
        }
    }

    public void removeGroup(String groupName) {
        groups.remove(groupName);
        save();
    }

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    public boolean hasPermission(PermissionGroup group, String node) {
        for (String n: group.permissions) {
            if (n.equals("-" + node)) {
                return false;
            }
            if (PermissionUtil.accessable(n, node)) {
                return true;
            }
        }
        for (String superGroupName: group.superGroups) {
            PermissionGroup superGroup = getGroup(superGroupName);
            if (Objects.isNull(superGroup)) {
                System.err.println("找不到权限组：" + superGroupName);
            }
            else if (hasPermission(superGroup, node)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeUserPermission(long qq, String node) {
        if (hasPermission(qq, node)) {
            PermissionUserNode userNode = getOrNewUserNode(qq);
            userNode.getPermissions().remove(node);

            if (hasPermission(qq, node)) {
                List<String> permissions = new ArrayList<>();
                permissions.add('-' + node);
                if (Objects.nonNull(userNode.getPermissions())) {
                    permissions.addAll(userNode.getPermissions());
                }
                userNode.setPermissions(permissions);
            }
            save();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hasPermission(long qq, String node) {
        PermissionUserNode permissionUserNode = getUserNode(qq);
        if (Objects.isNull(permissionUserNode)) {
            return hasPermission(getDefaultGroup(), node);
        }
        else if (permissionUserNode.hasPrivatePermission(node)) {
            return true;
        }
        else {
            return hasPermission(permissionUserNode.getGroup(), node);
        }
    }

    public boolean hasPermission(String groupName, String node) {
        PermissionGroup group = getGroup(groupName);
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