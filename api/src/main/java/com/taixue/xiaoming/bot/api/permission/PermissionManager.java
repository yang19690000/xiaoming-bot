package com.taixue.xiaoming.bot.api.permission;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface PermissionManager extends FileSavedData {
    String DEFAULT_PERMISSION_GROUP_NAME = "default";

    void setGroups(Map<String, PermissionGroup> groups);

    boolean userHasPermission(long qq,
                              String node);

    boolean userHasPermissions(long qq,
                               @NotNull String[] nodes);

    boolean groupHasPermission(String groupName, String node);

    @Nullable
    PermissionGroup getGroup(String groupName);

    boolean groupHasPermission(PermissionGroup group,
                               String node);

    void addGroup(String groupName, PermissionGroup group);

    void removeGroup(String groupName);

    Map<String, PermissionGroup> getGroups();

    boolean removeUserPermission(long qq, String node);

    @Nullable
    PermissionUserNode getUserNode(long qq);

    @Nullable
    PermissionUserNode getUserNode(@NotNull Account account);

    @NotNull
    PermissionUserNode getOrPutUserNode(long qq);

    @NotNull
    PermissionUserNode getOrPutUserNode(Account account);
}
