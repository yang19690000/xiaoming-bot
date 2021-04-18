package com.taixue.xiaomingbot.host.command.executor;

import com.taixue.xiaomingbot.api.command.*;
import com.taixue.xiaomingbot.api.group.Group;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.plugin.HookHolder;
import com.taixue.xiaomingbot.api.plugin.PluginConfig;
import com.taixue.xiaomingbot.api.plugin.PluginProperty;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.host.command.sender.GroupCommandSender;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
import com.taixue.xiaomingbot.util.CommandWordUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CoreCommandExecutor extends CommandExecutor {
    public String getCommandExecutorsString() {
        StringBuilder builder = new StringBuilder();
        XiaomingBot instance = XiaomingBot.getInstance();

        List<CommandExecutor> commandExecutors = instance.getCommandManager().getCommandExecutors();
        builder.append("指令处理器：");
        if (commandExecutors.isEmpty()) {
            builder.append("（无）");
        }
        else {
            for (CommandExecutor commandExecutor : commandExecutors) {
                String executorName;
                if (Objects.isNull(commandExecutor.getPlugin())) {
                    executorName = commandExecutor.getClass().getSimpleName() +
                            "(内核注册)";
                }
                else {
                    executorName = commandExecutor.getClass().getSimpleName() +
                            "(" + commandExecutor.getPlugin().getCompleteName() + ")";
                }
                builder.append("\n").append(executorName);
            }
        }

        return builder.toString();
    }

    public String getGroupInteractorString() {
        StringBuilder builder = new StringBuilder();
        XiaomingBot instance = XiaomingBot.getInstance();

        List<GroupInteractor> groupInteractors = instance.getGroupInteractorManager().getInteractors();
        builder.append("组消息交互器：");
        if (groupInteractors.isEmpty()) {
            builder.append("（无）");
        }
        else {
            for (GroupInteractor interactor : groupInteractors) {
                builder.append("\n")
                        .append(interactor.getClass().getName())
                        .append("(").append(interactor.getPlugin().getCompleteName()).append(")");
            }
        }

        return builder.toString();
    }

    public String getPrivateInteractorString() {
        StringBuilder builder = new StringBuilder();
        XiaomingBot instance = XiaomingBot.getInstance();

        List<PrivateInteractor> privateInteractors = instance.getPrivateInteractorManager().getInteractors();
        builder.append("私聊消息交互器：");
        if (privateInteractors.isEmpty()) {
            builder.append("（无）");
        }
        else {
            for (PrivateInteractor interactor : privateInteractors) {
                builder.append("\n")
                        .append(interactor.getClass().getName())
                        .append("(").append(interactor.getPlugin().getCompleteName()).append(")");
            }
        }

        return builder.toString();
    }

    public String getLoadedPluginString() {
        StringBuilder builder = new StringBuilder();
        XiaomingBot instance = XiaomingBot.getInstance();

        Map<String, XiaomingPlugin> loadedPlugins = instance.getPluginManager().getLoadedPlugins();
        builder.append("加载的插件：");
        if (loadedPlugins.isEmpty()) {
            builder.append("（无）");
        }
        else {
            for (XiaomingPlugin value : loadedPlugins.values()) {
                builder.append("\n").append(value.getCompleteName());
            }
        }

        return builder.toString();
    }

    @Override
    public String getCommandPrefix() {
        return "*";
    }

    @CommandFormat(CommandWordUtil.STATUS_REGEX + " " + CommandWordUtil.PLUGIN_REGEX)
    @RequiredPermission("plugin.list")
    public void onLoadedPluginsStatus(CommandSender sender) {
        sender.sendMessage(getLoadedPluginString());
    }

    @CommandFormat(CommandWordUtil.STATUS_REGEX + " 指令处理器")
    @RequiredPermission("plugin.list")
    public void onCommandExecutorStatus(CommandSender sender) {
        sender.sendMessage(getCommandExecutorsString());
    }

    @CommandFormat(CommandWordUtil.STATUS_REGEX + " 群聊交互器")
    @RequiredPermission("plugin.list")
    public void onGroupInteractorStatus(CommandSender sender) {
        sender.sendMessage(getGroupInteractorString());
    }

    @CommandFormat(CommandWordUtil.STATUS_REGEX + " 私聊交互器")
    @RequiredPermission("plugin.list")
    public void onPrivateInteractorStatus(CommandSender sender) {
        sender.sendMessage(getPrivateInteractorString());
    }

    @CommandFormat("调用")
    @CommandFormat("call")
    @CommandFormat("调用查询")
    public void onCallCounter(CommandSender sender) {
        sender.sendMessage("调用次数：{}", XiaomingBot.getInstance().getXiaomingConfig().getCallCouter());
    }

    @CommandFormat(CommandWordUtil.RELOAD_REGEX + " {what}")
    public void onReload(CommandSender sender,
                         @CommandParameter("what") String what) {
        switch (what) {
            case "群":
            case "群组":
                if (verifyPermissionAndReport(sender, "reload.group")) {
                    XiaomingBot.getInstance().reloadGroupManager();
                    sender.sendMessage("成功重载群组文件 (๑•̀ㅂ•́)و✧");
                }
                break;
            case "权限":
            case "权限组":
                if (verifyPermissionAndReport(sender, "reload.permission")) {
                    XiaomingBot.getInstance().reloadPermissionSystem();
                    sender.sendMessage("成功重载权限系统 (๑•̀ㅂ•́)و✧");
                }
                break;
            default:
                sender.sendError("小明不知道什么是重载 {} 呢", what);
                break;
        }
    }

    @CommandFormat(CommandWordUtil.GROUP_REGEX)
    public void onListGroup(CommandSender sender) {
        final GroupManager groupManager = XiaomingBot.getInstance().getGroupManager();
        final Map<String, Group> groups = groupManager.getGroups();
        if (Objects.isNull(groups) || groups.isEmpty()) {
            sender.sendMessage("小明没有载入任何 QQ 群组哦");
            return;
        }
        else {
            StringBuilder builder = new StringBuilder("小明载入了{}个QQ群：");
            for (Map.Entry<String, Group> entry : groups.entrySet()) {
                builder.append("\n");
                final Group value = entry.getValue();
                if (Objects.isNull(value.getAlias())) {
                    builder.append(entry.getKey()).append("：").append(value.getCode());
                }
                else {
                    builder.append(value.getAlias()).append("<").append(entry.getKey()).append(">").append("：").append(value.getCode());
                }
            }
            sender.sendMessage(builder.toString());
        }
    }

    @CommandFormat(CommandWordUtil.PLUGIN_REGEX + " {operator} {remain}")
    @CommandFormat(CommandWordUtil.PLUGIN_REGEX)
    public void onPlugin(CommandSender sender,
                         @CommandParameter("operator") String operator,
                         @CommandParameter("remain") String pluginName) {
        PluginManager pluginManager = XiaomingBot.getInstance().getPluginManager();
        switch (operator) {
            case "文件":
                if (verifyPermissionAndReport(sender, "plugin.file")) {
                    StringBuilder builder = new StringBuilder();

                    List<File> jarFiles = new ArrayList<>();
                    for (File file : pluginManager.directory.listFiles()) {
                        if (!file.isDirectory() && file.getName().endsWith(".jar")) {
                            jarFiles.add(file);
                        }
                    }

                    if (jarFiles.isEmpty()) {
                        builder.append("插件文件夹内没有任何插件，赶快添加一个吧 (๑•̀ㅂ•́)و✧");
                    }
                    else {
                        builder.append("插件文件夹内共有 " + jarFiles.size() + " 个插件文件：");
                        for (File jarFile : jarFiles) {
                            builder.append("\n").append(jarFile.getName());
                        }
                    }
                    sender.sendMessage(builder.toString());
                }
                break;
            case "加载":
                if (verifyPermissionAndReport(sender, "plugin.load")) {
                    if (pluginName.isEmpty()) {
                        pluginManager.reloadAll(sender);
                    }
                    else {
                        try {
                            File file = new File(pluginManager.directory, pluginName);
                            if (!file.exists() || file.isDirectory()) {
                                sender.sendError("应该给我的是插件文件名而不是插件名哦");
                                return;
                            }
                            PluginProperty property = pluginManager.pluginProperty(file);
                            if (Objects.isNull(property)) {
                                sender.sendError("看不懂 {} 这个插件文件");
                                return;
                            }
                            else {
                                if (pluginManager.isLoaded(property.getName())) {
                                    sender.sendMessage("{} 这个插件好像已经加载了，如果需要重载，告诉我「插件 重载 {}」哦",
                                            property.getName(), property.getName());
                                }
                                else {
                                    pluginManager.tryLoadPlugin(sender, new com.taixue.xiaomingbot.api.plugin.PluginManager.PluginLoader(file, property));
                                }
                            }
                        }
                        catch (IOException e) {
                            sender.sendError("小明好像无法正确读取 {} 这个文件", pluginName);
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "重载":
                if (verifyPermissionAndReport(sender, "plugin.reload")) {
                    if (pluginManager.isLoaded(pluginName)) {
                        pluginManager.reloadPlugin(sender, pluginName);
                    }
                    else {
                        sender.sendMessage("{} 这个插件还没有加载", pluginName);
                    }
                }
                break;
            case "卸载":
                if (verifyPermissionAndReport(sender, "plugin.unload")) {
                    if (pluginManager.isLoaded(pluginName)) {
                        pluginManager.unloadPlugin(sender, pluginName);
                    }
                    else {
                        sender.sendMessage("{} 这个插件还没有加载", pluginName);
                    }
                }
                break;
            case "查看":
            case "":
                if (pluginName.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    Map<String, XiaomingPlugin> loadedPlugins = pluginManager.getLoadedPlugins();

                    if (loadedPlugins.isEmpty()) {
                        builder.append("小明没有加载任何插件，试试「插件 加载 <插件文件名>」加载一个插件吧");
                    }
                    else {
                        builder.append("小明已加载了" + loadedPlugins.size() + "个插件：");
                        for (XiaomingPlugin value : loadedPlugins.values()) {
                            builder.append("\n").append(value.getName());
                        }
                    }
                    sender.sendMessage(builder.toString());
                }
                else {
                    XiaomingPlugin plugin = XiaomingBot.getInstance().getPluginManager().getPlugin(pluginName);
                    if (Objects.isNull(plugin)) {
                        sender.sendMessage("小明没有加载{}这个插件哦", pluginName);
                    }
                    else {
                        showPluginMessage(sender, plugin);
                    }
                }
                break;
            default:
                sender.sendError("我不知道什么是{}", operator);
                break;
        }
    }

    @CommandFormat("(禁用|关闭|屏蔽) {plugin} {remain}")
    @RequiredPermission("plugin.unload")
    public void onUnable(CommandSender sender,
                         @CommandParameter("plugin") String plugin,
                         @CommandParameter("remain") String group) {
        if (group.isEmpty() && sender instanceof GroupCommandSender) {
            group = ((GroupCommandSender) sender).getGroupCode() + "";
        }
        if (group.matches("\\d+")) {
            long groupCode = Long.parseLong(group);
            PluginConfig pluginConfig = XiaomingBot.getInstance().getPluginConfig();
            if (pluginConfig.unableInGroup(plugin, groupCode)) {
                sender.sendMessage("群 {} 中并不能使用小明插件 {}", group, plugin);
            }
            else {
                pluginConfig.toUnableInGroup(plugin, groupCode);
                sender.sendMessage("已在群 {} 中禁用小明插件 {}", group, plugin);
            }
        }
        else {
            sender.sendError("{} 好像不是一个群号哦", group);
            return;
        }
    }

    @CommandFormat("(启用|启动|开启) {plugin} {remain}")
    @RequiredPermission("plugin.unload")
    public void onEnable(CommandSender sender,
                         @CommandParameter("plugin") String plugin,
                         @CommandParameter("remain") String group) {
        if (group.isEmpty() && sender instanceof GroupCommandSender) {
            group = ((GroupCommandSender) sender).getGroupCode() + "";
        }
        if (group.matches("\\d+")) {
            long groupCode = Long.parseLong(group);
            PluginConfig pluginConfig = XiaomingBot.getInstance().getPluginConfig();
            if (pluginConfig.unableInGroup(plugin, groupCode)) {
                pluginConfig.enableInGroup(plugin, groupCode);
                sender.sendMessage("已启动群 {} 中的小明插件 {}", group, plugin);
            }
            else {
                sender.sendMessage("群 {} 中并没有禁用小明插件 {} 哦", group, plugin);
            }
        }
        else {
            sender.sendError("{} 好像不是一个群号哦", group);
            return;
        }
    }

    public void showPluginMessage(CommandSender sender, XiaomingPlugin plugin) {
        StringBuilder builder = new StringBuilder("【" + plugin.getName() + "】");
        builder.append("\n").append("版本：").append(plugin.getVersion());
        Map<String, HookHolder> hookHolders = plugin.getHookHolders();
        if (!hookHolders.isEmpty()) {
            builder.append("与 " + hookHolders.size() + " 个插件挂钩：");
            for (HookHolder value : hookHolders.values()) {
                builder.append("\n").append(value.getSponsor().getName());
            }
        }
        sender.sendMessage(builder.toString());
    }
}
