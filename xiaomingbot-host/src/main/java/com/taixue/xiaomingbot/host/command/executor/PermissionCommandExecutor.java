package com.taixue.xiaomingbot.host.command.executor;

import com.taixue.xiaomingbot.api.command.*;
import com.taixue.xiaomingbot.api.permission.PermissionGroup;
import com.taixue.xiaomingbot.api.permission.PermissionSystem;
import com.taixue.xiaomingbot.api.permission.PermissionUserNode;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.AtUtil;
import com.taixue.xiaomingbot.util.CommandWordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PermissionCommandExecutor extends CommandExecutor {

    public String getPermissionGroupName(String name) {
        PermissionGroup permissionGroup = XiaomingBot.getInstance().getPermissionSystem().getGroup(name);
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
    public void onNewPermissionGroup(CommandSender sender,
                                     @CommandParameter("name") String name) {
        PermissionSystem permissionSystem = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = permissionSystem.getGroup(name);
        if (Objects.nonNull(group)) {
            sender.sendMessage("权限组 {} 已经存在了（；´д｀）ゞ", getPermissionGroupName(name));
        }
        else {
            PermissionGroup permissionGroup = new PermissionGroup();
            List<String> superGroups = new ArrayList<>();
            superGroups.add(PermissionSystem.DEFAULT_PERMISSION_GROUP_NAME);
            permissionGroup.setSuperGroups(superGroups);
            permissionSystem.addGroup(name, permissionGroup);
            sender.sendMessage("已增加新的权限组：{}，小明已经将其继承自 {} 了",
                    name, getPermissionGroupName(PermissionSystem.DEFAULT_PERMISSION_GROUP_NAME));
        }
    }

    /**
     * 删除权限组
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " " + CommandWordUtil.REMOVE_REGEX + " {name}")
    @RequiredPermission("permission.group.remove")
    public void onRemovePermissionGroup(CommandSender sender,
                                        @CommandParameter("name") String name) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            sender.sendMessage("已删除权限组 {}", getPermissionGroupName(name));
            system.removeGroup(name);
        }
        else {
            sender.sendMessage("小明找不到权限组 {} (ノへ￣、)", getPermissionGroupName(name));
        }
    }

    /**
     * 设置权限组的别名
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.ALIAS_REGEX + " {alias}")
    @RequiredPermission("permission.group.alias")
    public void onSetGroupAlias(CommandSender sender,
                                @CommandParameter("name") String name,
                                @CommandParameter("alias") String alias) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            String elderAlias = group.getAlias();
            group.setAlias(alias);
            system.save();
            if (Objects.isNull(elderAlias)) {
                sender.sendMessage("已为权限组 {} 创建了备注：{}", name, alias);
            }
            else {
                sender.sendMessage("已将权限组 {} 的备注由 {} 改为：{}", name, elderAlias, alias);
            }
        }
        else {
            sender.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " " + CommandWordUtil.LOOK_REGEX + " {name}")
    @RequiredPermission("permission.group.look")
    public void onLookPermissionGroup(CommandSender sender,
                                      @CommandParameter("name") String name) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
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
            sender.sendMessage(builder.toString());
        }
        else {
            sender.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 授权给用户
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " 授权 {who} {node}")
    @CommandFormat("授权 {who} {node}")
    @RequiredPermission("permission.user.addnode")
    public void onGiveUserPermission(CommandSender sender,
                                     @CommandParameter("who") String who,
                                     @CommandParameter("node") String node) {

        long whoQQ = AtUtil.parseQQ(who);
        if (whoQQ == -1) {
            sender.sendMessage("找不到用户：{}", who);
            return;
        }
        else {
            sender.sendMessage("已授予 {} 权限节点：{}", who, node);
        }
    }

    /**
     * 增加组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.NEW_REGEX + " {node}")
    @RequiredPermission("permission.group.addnode")
    public void onAddGroupPermission(CommandSender sender,
                                     @CommandParameter("name") String name,
                                     @CommandParameter("node") String node) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            if (system.hasPermission(group, node)) {
                sender.sendMessage("权限组 {} 已经具有权限：{} 了", getPermissionGroupName(name), node);
            }
            else {
                group.addPermission(node);
                system.save();
                sender.sendMessage("成功为权限组 {} 增加了权限：{}", getPermissionGroupName(name), node);
            }
        }
        else {
            sender.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 确认组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.CONFIRM_REGEX + " {node}")
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} 确权 {node}")
    @RequiredPermission("permission.group.confirm")
    public void onConfirmGroupPermission(CommandSender sender,
                                         @CommandParameter("name") String name,
                                         @CommandParameter("node") String node) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            if (system.hasPermission(group, node)) {
                sender.sendMessage("权限组 {} 拥有权限：{}", getPermissionGroupName(name), node);
            }
            else {
                sender.sendMessage("权限组 {} 没有权限：{}", getPermissionGroupName(name), node);
            }
        }
        else {
            sender.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看权限组信息
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX)
    @RequiredPermission("permission.group.list")
    public void onListPermissionGroup(CommandSender sender) {
        Map<String, PermissionGroup> groups = XiaomingBot.getInstance().getPermissionSystem().getGroups();
        StringBuilder builder = new StringBuilder("当前共有 " + groups.size() + " 个权限组：");
        for (String groupName: groups.keySet()) {
            builder.append("\n").append(getPermissionGroupName(groupName));
        }
        sender.sendMessage(builder.toString());
    }

    /**
     * 确认玩家权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {who} " + CommandWordUtil.CONFIRM_REGEX + " {node}")
    @CommandFormat(CommandWordUtil.USER_REGEX + " {who} 确权 {node}")
    @RequiredPermission("permission.user.confirm")
    public void onConfirmUserPermission(CommandSender sender,
                                        @CommandParameter("who") String who,
                                        @CommandParameter("node") String node) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        long qq = AtUtil.parseQQ(who);
        if (qq == -1) {
            sender.sendMessage("找不到玩家：{}", who);
        }

        if (system.hasPermission(qq, node)) {
            sender.sendMessage("用户 {} 拥有权限：{}", who, node);
        }
        else {
            sender.sendMessage("用户 {} 没有权限：{}", who, node);
        }
    }

    /**
     * 删除玩家权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {who} " + CommandWordUtil.REMOVE_REGEX + " {node}")
    @RequiredPermission("permission.user.remove")
    public void onRemoveUserPermission(CommandSender sender,
                                       @CommandParameter("who") String who,
                                       @CommandParameter("node") String node) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        long qq = AtUtil.parseQQ(who);
        if (qq == -1) {
            sender.sendMessage("找不到用户：{}", who);
        }
        if (system.removeUserPermission(qq, node)) {
            sender.sendMessage("已移除用户 {} 的权限：{}", who, node);
        }
        else {
            sender.sendMessage("{} 并没有权限：{} 哦", who, node);
        }
    }

    /**
     * 删除组权限
     */
    @CommandFormat(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.REMOVE_REGEX + " {node}")
    @RequiredPermission("permission.group.remove")
    public void onRemoveGroupPermission(CommandSender sender,
                                        @CommandParameter("name") String name,
                                        @CommandParameter("node") String node) {
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionGroup group = system.getGroup(name);

        if (node.startsWith("-")) {
            sender.sendError("{} 并不是一个合理的权限节点哦", node);
            return;
        }

        if (Objects.nonNull(group)) {
            if (!system.hasPermission(group, node)) {
                sender.sendMessage("权限组 {} 并不具有 {} 的权限哦", getPermissionGroupName(name), node);
            }
            else {
                group.removePermission(node);
                system.save();
                if (system.hasPermission(group, node)) {
                    List<String> permissions = new ArrayList<>();
                    permissions.add('-' + node);
                    permissions.addAll(group.getPermissions());
                    group.setPermissions(permissions);

                    sender.sendMessage("成功移除了权限组 {} 的权限：{}，但是其父类仍具有改权限。" +
                            "小明已经帮你增加了权限节点：-{} 以删除此权限。", getPermissionGroupName(name), node, node);
                }
                else {
                    sender.sendMessage("成功移除了权限组 {} 的权限：{}", getPermissionGroupName(name), node);
                }
            }
        }
        else {
            sender.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看用户权限
     */
    @CommandFormat(CommandWordUtil.USER_REGEX + " {who} " + CommandWordUtil.LOOK_REGEX)
    @CommandFormat(CommandWordUtil.USER_REGEX + " {who}")
    @RequiredPermission("permission.user.look")
    public void onLookUserPermission(CommandSender sender,
                                     @CommandParameter("who") String who) {
        long qq = AtUtil.parseQQ(who);
        if (qq == -1) {
            sender.sendMessage("似乎 {} 不是一个用户", who);
            return;
        }
        PermissionSystem system = XiaomingBot.getInstance().getPermissionSystem();
        PermissionUserNode userNode = system.getUserNode(qq);
        if (Objects.isNull(userNode)) {
            sender.sendMessage("用户权限信息：\n" +
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
            sender.sendMessage(builder.toString());
        }
    }
}
