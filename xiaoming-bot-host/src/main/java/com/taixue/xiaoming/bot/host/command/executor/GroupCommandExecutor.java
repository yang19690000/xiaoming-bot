package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class GroupCommandExecutor extends CommandExecutor {
    private final GroupManager groupManager = getXiaomingBot().getGroupManager();
    private static final String TAG_REGEX = "(标记|标注|tag)";

    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.GROUP_REGEX;
    }

    public String getGroupName(@NotNull final Group group) {
        if (Objects.isNull(group.getAlias())) {
            return group.getCode() + "";
        } else {
            return group.getAlias() + "（" + group.getCode() + "）";
        }
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + " {key} " + TAG_REGEX)
    @RequiredPermission("group.tag.list")
    public void onListGroupTags(@NotNull final XiaomingUser user,
                                @NotNull @CommandParameter("key") String key) {
        final Group group = groupManager.getGroups().get(key);
        if (Objects.isNull(group)) {
            user.sendError("找不到响应群：{}", key);
            return;
        }
        user.sendMessage("{}的标记有：{}", getGroupName(group), group.getTags());
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + TAG_REGEX)
    @RequiredPermission("group.tag.list")
    public void onListGroupTags(@NotNull final GroupXiaomingUser user,
                                @NotNull @CommandParameter("key") String key) {
        final Group group = groupManager.forGroup(user.getGroup());
        if (Objects.isNull(group)) {
            user.sendError("意外：本群不是小明的响应群");
            return;
        }
        if (Objects.isNull(group)) {
            user.sendError("找不到响应群：{}", key);
            return;
        }
        user.sendMessage("{}的标记有：{}", getGroupName(group), group.getTags());
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + " {key} " + TAG_REGEX + " " + CommandWordUtil.NEW_REGEX + " {tag}")
    @RequiredPermission("group.tag.add")
    public void onAddGroupTag(@NotNull final XiaomingUser user,
                              @NotNull @CommandParameter("key") String key,
                              @NotNull @CommandParameter("tag") String tag) {
        final Group group = groupManager.getGroups().get(key);
        if (Objects.isNull(group)) {
            user.sendError("找不到响应群：{}", key);
            return;
        }
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("{}已经有这个标记了哦", getGroupName(group));
        } else {
            tags.add(tag);
            groupManager.save();
            user.sendMessage("成功为{}添加了新的标记：{}", getGroupName(group), tag);
        }
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + TAG_REGEX + " " + CommandWordUtil.NEW_REGEX + " {tag}")
    @RequiredPermission("group.tag.add")
    public void onAddThisGroupTag(@NotNull final GroupXiaomingUser user,
                                  @NotNull @CommandParameter("tag") String tag) {
        final Group group = groupManager.forGroup(user.getGroup());
        if (Objects.isNull(group)) {
            user.sendError("意外：本群不是小明的响应群");
            return;
        }
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("本群已经有这个标记了哦");
        } else {
            tags.add(tag);
            groupManager.save();
            user.sendMessage("成功本群添加了新的标记：{}", tag);
        }
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + " " + CommandWordUtil.BLOCK_REGEX + " {plugin}")
    @RequiredPermission("group.plugin.block")
    public void onBlockPlugin(@NotNull final GroupXiaomingUser user,
                              @NotNull @CommandParameter("plugin") final String plugin) {
        Group group = groupManager.forGroup(user.getGroup());
        if (group.isUnablePlugin(plugin)) {
            user.sendError("本群已经屏蔽了插件{}", plugin);
        } else {
            group.addBlockPlugin(plugin);
            groupManager.save();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群屏蔽了插件{}", plugin);
            } else {
                user.sendError("成功在本群屏蔽了插件{}，但该插件还没有加载", plugin);
            }
        }
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX + " " + CommandWordUtil.UNBLOCK_REGEX + " {plugin}")
    @RequiredPermission("group.plugin.unblock")
    public void onUnblockPlugin(@NotNull final GroupXiaomingUser user,
                                @NotNull @CommandParameter("plugin") final String plugin) {
        Group group = groupManager.forGroup(user.getGroup());
        if (group.isUnablePlugin(plugin)) {
            group.getBlockPlugins().remove(plugin);
            groupManager.save();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群取消屏蔽插件{}", plugin);
            } else {
                user.sendError("成功在本群取消屏蔽插件{}，但该插件还没有加载", plugin);
            }
        } else {
            user.sendError("本群还没有屏蔽插件{}", plugin);
        }
    }
}
