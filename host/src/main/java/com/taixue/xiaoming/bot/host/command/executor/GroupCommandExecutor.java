package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.util.CommandWordUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class GroupCommandExecutor extends CommandExecutorImpl {
    private final GroupManager groupManager = getXiaomingBot().getGroupManager();
    private static final String TAG_REGEX = "(标记|标注|tag)";

    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.GROUP_REGEX;
    }

    public String getGroupName(final Group group) {
        if (Objects.isNull(group.getAlias())) {
            return group.getCode() + "";
        } else {
            return group.getAlias() + "（" + group.getCode() + "）";
        }
    }

    @Command(CommandWordUtil.GROUP_REGEX)
    @RequirePermission("group.list")
    public void onListGroups(final XiaomingUser user) {
        final Map<String, Group> groups = groupManager.getGroups();
        if (groups.isEmpty()) {

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("小明的响应群有 ").append(groups.size()).append("个：");
            for (Map.Entry<String, Group> entry : groups.entrySet()) {
                builder.append("\n")
                        .append(getGroupName(entry.getValue())).append("：").append(entry.getKey());
            }
            user.sendMessage(builder.toString());
        }

    }

    @Command(CommandWordUtil.GROUP_REGEX + " {group}")
    @RequirePermission("group.look")
    public void onLookGroup(final XiaomingUser user,
                            @CommandParameter("group") final String groupString) {
        Group group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forGroup(Long.parseLong(groupString));
        } else {
            group = groupManager.forName(groupString);
        }

        if (Objects.isNull(group)) {
            user.sendMessage("这个群并不是小明的响应群哦");
            return;
        } else {
            user.sendMessage("群备注：{}\n" +
                            "群号：{}\n" +
                            "群标记：{}\n" +
                            "屏蔽的插件：{}",
                    Objects.nonNull(group.getAlias()) ? group.getAlias() : "（无）",
                    group.getCode(),
                    group.getTags(),
                    group.getBlockPlugins());
        }
    }

    @Command(CommandWordUtil.REMOVE_REGEX + CommandWordUtil.GROUP_REGEX + " {group}")
    @RequirePermission("group.remove")
    public void onRemoveGroup(final XiaomingUser user,
                              @CommandParameter("group") final String groupString) {
        Group group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forGroup(Long.parseLong(groupString));
        } else {
            group = groupManager.forName(groupString);
        }

        if (Objects.isNull(group)) {
            user.sendMessage("这个群并不是小明的响应群哦");
        } else {
            user.sendMessage("成功移除小明响应群：", getGroupName(group));
            groupManager.getGroups().remove(group);
            groupManager.readySave();
        }
    }

    @Command(CommandWordUtil.THIS_REGEX + CommandWordUtil.GROUP_REGEX)
    @RequirePermission("group.look")
    public void onLookThisGroup(final GroupXiaomingUser user) {
        Group group = groupManager.forGroup(user.getGroup());

        user.sendMessage("本群备注：{}\n" +
                        "群号：{}\n" +
                        "群标记：{}\n" +
                        "屏蔽的插件：{}",
                Objects.nonNull(group.getAlias()) ? group.getAlias() : "（无）",
                group.getCode(),
                group.getTags(),
                group.getBlockPlugins());
    }

    @Command(CommandWordUtil.GROUP_REGEX + " {key} " + TAG_REGEX)
    @RequirePermission("group.tag.list")
    public void onListGroupTags(final XiaomingUser user,
                                @CommandParameter("key") final String key) {
        final Group group = groupManager.getGroups().get(key);
        if (Objects.isNull(group)) {
            user.sendError("找不到响应群：{}", key);
            return;
        }
        user.sendMessage("{}的标记有：{}", getGroupName(group), group.getTags());
    }

    @Command(CommandWordUtil.GROUP_REGEX + TAG_REGEX)
    @RequirePermission("group.tag.list")
    public void onListGroupTags(final GroupXiaomingUser user,
                                @CommandParameter("key") final String key) {
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

    @Command(CommandWordUtil.GROUP_REGEX + " {key} " + TAG_REGEX + " " + CommandWordUtil.NEW_REGEX + " {tag}")
    @RequirePermission("group.tag.add")
    public void onAddGroupTag(final XiaomingUser user,
                              @CommandParameter("key") final String key,
                              @CommandParameter("tag") final String tag) {
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
            groupManager.readySave();
            user.sendMessage("成功为{}添加了新的标记：{}", getGroupName(group), tag);
        }
    }

    @Command(CommandWordUtil.NEW_REGEX + CommandWordUtil.GROUP_REGEX + TAG_REGEX + " " + " {tag}")
    @RequirePermission("group.tag.add")
    public void onAddGroupTag(final GroupXiaomingUser user,
                              @CommandParameter("tag") final String tag) {
        final Group group = groupManager.getGroups().get(user.getGroup());
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("本群已经有这个标记了哦");
        } else {
            tags.add(tag);
            groupManager.readySave();
            user.sendMessage("成功为本群添加了新的标记：{}", tag);
        }
    }

    @Command(CommandWordUtil.GROUP_REGEX + " " + CommandWordUtil.BLOCK_REGEX + " {plugin}")
    @RequirePermission("group.plugin.block")
    public void onBlockPlugin(final GroupXiaomingUser user,
                              @CommandParameter("plugin") final String plugin) {
        Group group = groupManager.forGroup(user.getGroup());
        if (group.isUnablePlugin(plugin)) {
            user.sendError("本群已经屏蔽了插件{}", plugin);
        } else {
            group.addBlockPlugin(plugin);
            groupManager.readySave();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群屏蔽了插件{}", plugin);
            } else {
                user.sendError("成功在本群屏蔽了插件{}，但该插件还没有加载", plugin);
            }
        }
    }

    @Command(CommandWordUtil.GROUP_REGEX + " " + CommandWordUtil.UNBLOCK_REGEX + " {plugin}")
    @RequirePermission("group.plugin.unblock")
    public void onUnblockPlugin(final GroupXiaomingUser user,
                                @CommandParameter("plugin") final String plugin) {
        Group group = groupManager.forGroup(user.getGroup());
        if (group.isUnablePlugin(plugin)) {
            group.getBlockPlugins().remove(plugin);
            groupManager.readySave();
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
