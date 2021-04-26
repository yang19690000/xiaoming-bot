package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.permission.PermissionGroup;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.permission.PermissionUserNode;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.AtUtil;
import com.taixue.xiaoming.bot.util.CommandWordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PermissionCommandExecutor extends CommandExecutor {
    private final PermissionManager permissionManager = getXiaomingBot().getPermissionManager();
    private static final String SET = "(委任|指派|委派|任命|set)";

    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.PERMISSION_GROUP_REGEX;
    }

    public String getPermissionGroupName(String name) {
        PermissionGroup permissionGroup = permissionManager.getGroup(name);
        if (Objects.isNull(permissionGroup) || Objects.isNull(permissionGroup.getAlias())) {
            return name;
        }
        else {
            return permissionGroup.getAlias() + "（" + name + "）";
        }
    }

    /**
     * 新增权限组
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " " + CommandWordUtil.NEW_REGEX + " {name}")
    @RequiredPermission("permission.group.new")
    public void onNewPermissionGroup(final XiaomingUser user,
                                     @CommandParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("权限组 {} 已经存在了（；´д｀）ゞ", getPermissionGroupName(name));
        }
        else {
            PermissionGroup permissionGroup = new PermissionGroup();
            List<String> superGroups = new ArrayList<>();
            superGroups.add(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME);
            permissionGroup.setSuperGroups(superGroups);
            permissionManager.addGroup(name, permissionGroup);
            user.sendMessage("已增加新的权限组：{}，小明已经将其继承自 {} 了",
                    name, getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME));
        }
    }

    /**
     * 设置用户权限组
     */
    @CommandFormat(SET + " {qq} {group}")
    @RequiredPermission("permission.user.set")
    public void onSetUserGroup(final XiaomingUser user,
                               @CommandParameter("qq") final long qq,
                               @CommandParameter("group") final String group) {
        PermissionGroup permissionGroup = permissionManager.getGroup(group);
        if (Objects.isNull(group)) {
            user.sendMessage("找不到权限组 {}（；´д｀）ゞ", group);
        }
        else {
            final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
            permissionManager.getOrPutUserNode(account).setGroup(group);
            account.save();

            user.sendMessage("成功设置用户的权限组为：{}", getPermissionGroupName(group));
        }
    }

    /**
     * 删除权限组
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " " + CommandWordUtil.REMOVE_REGEX + " {name}")
    @RequiredPermission("permission.group.remove")
    public void onRemovePermissionGroup(final XiaomingUser user,
                                        @CommandParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("已删除权限组 {}", getPermissionGroupName(name));
            permissionManager.removeGroup(name);
        }
        else {
            user.sendMessage("小明找不到权限组 {} (ノへ￣、)", getPermissionGroupName(name));
        }
    }

    /**
     * 设置权限组的别名
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.ALIAS_REGEX + " {alias}")
    @RequiredPermission("permission.group.alias")
    public void onSetGroupAlias(final XiaomingUser user,
                                @CommandParameter("name") String name,
                                @CommandParameter("alias") String alias) {
        PermissionManager system = permissionManager;
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            String elderAlias = group.getAlias();
            group.setAlias(alias);
            system.save();
            if (Objects.isNull(elderAlias)) {
                user.sendMessage("已为权限组 {} 创建了备注：{}", name, alias);
            }
            else {
                user.sendMessage("已将权限组 {} 的备注由 {} 改为：{}", name, elderAlias, alias);
            }
        }
        else {
            user.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " " + CommandWordUtil.LOOK_REGEX + " {name}")
    @RequiredPermission("permission.group.look")
    public void onLookPermissionGroup(final XiaomingUser user,
                                      @CommandParameter("name") String name) {
        PermissionManager system = permissionManager;
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            StringBuilder builder = new StringBuilder("【权限组信息】");
            builder.append("\n").append("权限组名：").append(name)
                    .append("\n").append("备注：").append(Objects.isNull(group.getAlias()) ? "（无）" : group.getAlias())
                    .append("\n").append("父权限组：");
            if (group.getSuperGroups().isEmpty()) {
                builder.append("（无）");
            }
            else {
                for (String s: group.getSuperGroups()) {
                    builder.append("\n").append(getPermissionGroupName(s));
                }
            }
            builder.append("\n").append("权限节点：");
            if (group.getPermissions().isEmpty()) {
                builder.append("（无）");
            }
            else {
                for (String node: group.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        }
        else {
            user.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 授权给用户
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " 授权 {qq} {node}")
    @CommandFormat("授权 {qq} {node}")
    @RequiredPermission("permission.user.addnode")
    public void onGiveUserPermission(final XiaomingUser user,
                                     @CommandParameter("qq") final long qq,
                                     @CommandParameter("node") String node) {
        final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(account);
        userNode.addPermission(node);
        account.save();
        user.sendMessage("已授予 {} 权限节点：{}", qq, node);
    }

    /**
     * 增加组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.NEW_REGEX + " {node}")
    @RequiredPermission("permission.group.addnode")
    public void onAddGroupPermission(final XiaomingUser user,
                                     @CommandParameter("name") String name,
                                     @CommandParameter("node") String node) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            if (permissionManager.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 已经具有权限：{} 了", getPermissionGroupName(name), node);
            }
            else {
                group.addPermission(node);
                permissionManager.save();
                user.sendMessage("成功为权限组 {} 增加了权限：{}", getPermissionGroupName(name), node);
            }
        }
        else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 确认组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.CONFIRM_REGEX + " {node}")
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} 确权 {node}")
    @RequiredPermission("permission.group.confirm")
    public void onConfirmGroupPermission(final XiaomingUser user,
                                         @CommandParameter("name") String name,
                                         @CommandParameter("node") String node) {
        PermissionManager system = permissionManager;
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            if (system.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 拥有权限：{}", getPermissionGroupName(name), node);
            }
            else {
                user.sendMessage("权限组 {} 没有权限：{}", getPermissionGroupName(name), node);
            }
        }
        else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看权限组信息
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX)
    @RequiredPermission("permission.group.list")
    public void onListPermissionGroup(final XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        StringBuilder builder = new StringBuilder("当前共有 " + groups.size() + " 个权限组：");
        for (String groupName: groups.keySet()) {
            builder.append("\n").append(getPermissionGroupName(groupName));
        }
        user.sendMessage(builder.toString());
    }

    /**
     * 确认玩家权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {qq} " + CommandWordUtil.CONFIRM_REGEX + " {node}")
    @CommandFormat(CommandWordUtil.USER_REGEX + " {qq} 确权 {node}")
    @RequiredPermission("permission.user.confirm")
    public void onConfirmUserPermission(final XiaomingUser user,
                                        @CommandParameter("qq") final long qq,
                                        @CommandParameter("node") String node) {
        if (permissionManager.userHasPermission(qq, node)) {
            user.sendMessage("用户 {} 拥有权限：{}", qq, node);
        }
        else {
            user.sendMessage("用户 {} 没有权限：{}", qq, node);
        }
    }

    /**
     * 删除玩家权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {qq} " + CommandWordUtil.REMOVE_REGEX + " {node}")
    @RequiredPermission("permission.user.remove")
    public void onRemoveUserPermission(final XiaomingUser user,
                                       @CommandParameter("qq") final long qq,
                                       @CommandParameter("node") String node) {
        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("已移除用户 {} 的权限：{}", qq, node);
        }
        else {
            user.sendMessage("{} 并没有权限：{} 哦", qq, node);
        }
    }

    /**
     * 删除组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.REMOVE_REGEX + " {node}")
    @RequiredPermission("permission.group.remove")
    public void onRemoveGroupPermission(final XiaomingUser user,
                                        @CommandParameter("name") String name,
                                        @CommandParameter("node") String node) {
        PermissionGroup group = permissionManager.getGroup(name);

        if (node.startsWith("-")) {
            user.sendError("{} 并不是一个合理的权限节点哦", node);
            return;
        }

        if (Objects.nonNull(group)) {
            if (!permissionManager.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 并不具有 {} 的权限哦", getPermissionGroupName(name), node);
            }
            else {
                group.removePermission(node);
                permissionManager.save();
                if (permissionManager.groupHasPermission(group, node)) {
                    List<String> permissions = new ArrayList<>();
                    permissions.add('-' + node);
                    permissions.addAll(group.getPermissions());
                    group.setPermissions(permissions);

                    user.sendMessage("成功移除了权限组 {} 的权限：{}，但是其父类仍具有该权限。" +
                            "小明已经帮你增加了权限节点：-{} 以删除此权限。", getPermissionGroupName(name), node, node);
                }
                else {
                    user.sendMessage("成功移除了权限组 {} 的权限：{}", getPermissionGroupName(name), node);
                }
            }
        }
        else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看用户权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {qq} " + CommandWordUtil.LOOK_REGEX)
    @CommandFormat(CommandWordUtil.USER_REGEX + " {qq}")
    @RequiredPermission("permission.user.look")
    public void onLookUserPermission(final XiaomingUser user,
                                     @CommandParameter("qq") final long qq) {
        PermissionUserNode userNode = permissionManager.getUserNode(qq);
        if (Objects.isNull(userNode)) {
            user.sendMessage("用户权限信息：\n" +
                    "所属组：{}", getPermissionGroupName("default"));
        }
        else {
            StringBuilder builder = new StringBuilder("用户权限信息：");
            builder.append("\n").append("所属组：" + getPermissionGroupName(userNode.getGroup()));
            if (Objects.isNull(userNode.getPermissions()) || userNode.getPermissions().isEmpty()) {
                builder.append("\n").append("没有其他特有权限。");
            }
            else {
                builder.append("\n").append("特有权限（" + userNode.getPermissions().size() + "条）：");
                for (String node: userNode.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        }
    }
}
