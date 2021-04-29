package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.permission.PermissionGroup;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.permission.PermissionUserNode;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.core.permission.PermissionGroupImpl;
import com.taixue.xiaoming.bot.util.CommandWordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PermissionCommandExecutor extends CommandExecutorImpl {
    private final PermissionManager permissionManager = getXiaomingBot().getPermissionManager();
    private static final String LET = "(委任|指派|委派|任命|let)";
    private static final String INHERIT = "(继承|扩展|继承自|extends|inherit)";
    private static final String EXTENDS = "(派生|derive)";
    private static final String GRANT = "(授权|grant)";
    private static final String REVOKE = CommandWordUtil.REMOVE_REGEX + GRANT;
    private static final String PERMISSION_CONFIRM = CommandWordUtil.PERMISSION_GROUP_REGEX + CommandWordUtil.CONFIRM_REGEX;

    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.PERMISSION_GROUP_REGEX;
    }

    public String getPermissionGroupName(String name) {
        PermissionGroup permissionGroup = permissionManager.getGroup(name);
        if (Objects.isNull(permissionGroup) || Objects.isNull(permissionGroup.getAlias())) {
            return name;
        } else {
            return permissionGroup.getAlias() + "（" + name + "）";
        }
    }

    /**
     * 新增权限组
     */
    @Command(CommandWordUtil.NEW_REGEX + CommandWordUtil.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.new")
    public void onNewPermissionGroup(final XiaomingUser user,
                                     @CommandParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("权限组 {} 已经存在了（；´д｀）ゞ", getPermissionGroupName(name));
        } else {
            PermissionGroup permissionGroup = new PermissionGroupImpl();
            List<String> superGroups = new ArrayList<>();
            superGroups.add(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME);
            permissionGroup.setSuperGroups(superGroups);
            permissionManager.addGroup(name, permissionGroup);
            permissionManager.save();
            user.sendMessage("已增加新的权限组：{}，小明已经将其继承自 {} 了",
                    name, getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME));
        }
    }

    /**
     * 设置用户权限组
     */
    @Command(LET + " {qq} {group}")
    @RequirePermission("permission.user.set")
    public void onSetUserGroup(final XiaomingUser user,
                               @CommandParameter("qq") final long qq,
                               @CommandParameter("group") final String group) {
        PermissionGroup permissionGroup = permissionManager.getGroup(group);
        if (Objects.isNull(group)) {
            user.sendMessage("找不到权限组 {}（；´д｀）ゞ", group);
        } else {
            final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
            permissionManager.getOrPutUserNode(account).setGroup(group);
            account.save();
            user.sendMessage("成功设置用户的权限组为：{}", getPermissionGroupName(group));
        }
    }

    /**
     * 删除权限组
     */
    @Command(CommandWordUtil.REMOVE_REGEX + CommandWordUtil.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.remove")
    public void onRemovePermissionGroup(final XiaomingUser user,
                                        @CommandParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("已删除权限组 {}", getPermissionGroupName(name));
            permissionManager.removeGroup(name);
            permissionManager.save();
        } else {
            user.sendMessage("小明找不到权限组 {} (ノへ￣、)", getPermissionGroupName(name));
        }
    }

    /**
     * 设置权限组的别名
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.ALIAS_REGEX + " {alias}")
    @RequirePermission("permission.group.alias")
    public void onSetGroupAlias(final XiaomingUser user,
                                @CommandParameter("name") String name,
                                @CommandParameter("alias") String alias) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            String elderAlias = group.getAlias();
            group.setAlias(alias);
            permissionManager.save();
            if (Objects.isNull(elderAlias)) {
                user.sendMessage("已为权限组{}创建了备注：{}", name, alias);
            } else {
                user.sendMessage("已将权限组{}的备注由 {}改为：{}", name, elderAlias, alias);
            }
        } else {
            user.sendError("小明找不到权限组{}", name);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.look")
    public void onLookPermissionGroup(final XiaomingUser user,
                                      @CommandParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            StringBuilder builder = new StringBuilder("【权限组信息】");
            builder.append("\n").append("权限组名：").append(name)
                    .append("\n").append("备注：").append(Objects.isNull(group.getAlias()) ? "（无）" : group.getAlias())
                    .append("\n").append("父权限组：");
            if (group.getSuperGroups().isEmpty()) {
                builder.append("（无）");
            } else {
                for (String s : group.getSuperGroups()) {
                    builder.append("\n").append(getPermissionGroupName(s));
                }
            }
            builder.append("\n").append("权限节点：");
            if (group.getPermissions().isEmpty()) {
                builder.append("（无）");
            } else {
                for (String node : group.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        } else {
            user.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 授权给用户
     */
    @Command(GRANT + " {qq} {node}")
    public void onGiveUserPermission(final XiaomingUser user,
                                     @CommandParameter("qq") final long qq,
                                     @CommandParameter("node") String node) {
        if (!verifyPermissionAndReport(user, "permission.user.add." + node)) {
            return;
        }
        final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(account);
        userNode.addPermission(node);
        account.save();
        user.sendMessage("已授予 {} 权限节点：{}", qq, node);
    }

    /**
     * 增加组权限
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.NEW_REGEX + " {node}")
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + GRANT + " {node}")
    @RequirePermission("permission.group.add")
    public void onAddGroupPermission(final XiaomingUser user,
                                     @CommandParameter("name") String name,
                                     @CommandParameter("node") String node) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            if (permissionManager.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 已经具有权限：{} 了", getPermissionGroupName(name), node);
            } else {
                group.addPermission(node);
                permissionManager.save();
                user.sendMessage("成功为权限组 {} 增加了权限：{}", getPermissionGroupName(name), node);
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 确认组权限
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.group.confirm")
    public void onConfirmGroupPermission(final XiaomingUser user,
                                         @CommandParameter("name") String name,
                                         @CommandParameter("node") String node) {
        PermissionManager system = permissionManager;
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            if (system.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 拥有权限：{}", getPermissionGroupName(name), node);
            } else {
                user.sendMessage("权限组 {} 没有权限：{}", getPermissionGroupName(name), node);
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看所有的权限组
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX)
    @RequirePermission("permission.group.list")
    public void onListPermissionGroup(final XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        StringBuilder builder = new StringBuilder("当前共有 " + groups.size() + " 个权限组：");
        for (String groupName : groups.keySet()) {
            builder.append("\n").append(getPermissionGroupName(groupName));
        }
        user.sendMessage(builder.toString());
    }

    /**
     * 确认玩家权限
     */
    @Command(CommandWordUtil.USER_REGEX + " {qq} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.user.confirm")
    public void onConfirmUserPermission(final XiaomingUser user,
                                        @CommandParameter("qq") final long qq,
                                        @CommandParameter("node") String node) {
        if (permissionManager.userHasPermission(qq, node)) {
            user.sendMessage("用户 {} 拥有权限：{}", qq, node);
        } else {
            user.sendMessage("用户 {} 没有权限：{}", qq, node);
        }
    }

    /**
     * 删除玩家权限
     */
    @Command(REVOKE + " {qq} " + " {node}")
    public void onRemoveUserPermission(final QQXiaomingUser user,
                                       @CommandParameter("qq") final long qq,
                                       @CommandParameter("node") String node) {
        if (!verifyPermissionAndReport(user, "permission.user.remove." + node)) {
            return;
        }
        final Account account = user.getOrPutAccount();
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(account);
        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("已移除用户 {} 的权限：{}", qq, node);
        } else {
            user.sendMessage("{} 并没有权限：{} 哦", qq, node);
        }
    }

    /**
     * 删除组权限
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + CommandWordUtil.REMOVE_REGEX + " {node}")
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {name} " + REVOKE + " {node}")
    @RequirePermission("permission.group.remove")
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
            } else {
                group.removePermission(node);
                permissionManager.save();
                if (permissionManager.groupHasPermission(group, node)) {
                    List<String> permissions = new ArrayList<>();
                    permissions.add('-' + node);
                    permissions.addAll(group.getPermissions());
                    group.setPermissions(permissions);

                    user.sendMessage("成功移除了权限组 {} 的权限：{}，但是其父类仍具有该权限。" +
                            "小明已经帮你增加了权限节点：-{} 以删除此权限。", getPermissionGroupName(name), node, node);
                } else {
                    user.sendMessage("成功移除了权限组 {} 的权限：{}", getPermissionGroupName(name), node);
                }
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看用户权限
     */
    @Command(CommandWordUtil.USER_REGEX + CommandWordUtil.PERMISSION_GROUP_REGEX + " {qq}")
    @RequirePermission("permission.user.look")
    public void onLookUserPermission(final XiaomingUser user,
                                     @CommandParameter("qq") final long qq) {
        PermissionUserNode userNode = permissionManager.getUserNode(qq);
        if (Objects.isNull(userNode)) {
            user.sendMessage("用户权限信息：\n" +
                    "所属组：{}", getPermissionGroupName("default"));
        } else {
            StringBuilder builder = new StringBuilder("用户权限信息：");
            builder.append("\n").append("所属组：" + getPermissionGroupName(userNode.getGroup()));
            if (Objects.isNull(userNode.getPermissions()) || userNode.getPermissions().isEmpty()) {
                builder.append("\n").append("没有其他特有权限。");
            } else {
                builder.append("\n").append("特有权限（" + userNode.getPermissions().size() + "条）：");
                for (String node : userNode.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        }
    }

    /**
     * 设置继承关系
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {super} " + EXTENDS + " {son}")
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {son} " + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.new")
    public void onAddGroupSuper(final XiaomingUser user,
                                @CommandParameter("super") final String superGroupName,
                                @CommandParameter("son") final String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("找不到父权限组{}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("找不到子权限组{}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            user.sendError("{}已经是{}的父类了，无须重复继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
            return;
        } else if (permissionManager.isSuper(sonGroupName, superGroupName)) {
            user.sendError("{}已经是{}的父类了，无法相互继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        } else {
            sonGroup.getSuperGroups().add(superGroupName);
            permissionManager.save();
            user.sendMessage("成功令{}继承了{}的所有权限", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        }
    }

    /**
     * 取消继承关系
     */
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {super} " + CommandWordUtil.CANCEL_REGEX + EXTENDS + " {son}")
    @Command(CommandWordUtil.PERMISSION_GROUP_REGEX + " {son} " + CommandWordUtil.CANCEL_REGEX + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.cancel")
    public void onRemoveGroupSuper(final XiaomingUser user,
                                   @CommandParameter("super") final String superGroupName,
                                   @CommandParameter("son") final String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("找不到父权限组{}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("找不到子权限组{}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            sonGroup.getSuperGroups().remove(superGroupName);
            permissionManager.save();
            user.sendMessage("成功取消了{}派生{}的联系", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        } else {

            user.sendError("{}并不是{}的父权限组哦", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        }
    }
}