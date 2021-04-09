package com.taixue.xiaomingbot.host.command.executor;

import com.taixue.xiaomingbot.api.command.*;
import com.taixue.xiaomingbot.api.plugin.HookHolder;
import com.taixue.xiaomingbot.api.plugin.PluginProperty;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.host.command.sender.ConsoleCommandSender;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
import com.taixue.xiaomingbot.util.AtUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CoreCommandExecutor extends CommandExecutor {
    public static final String RELOAD_WORD_REGEX = "(重载|重加载|重新加载)";
    public static final String PLUGIN_WORD_REGEX = "插件";

    @Override
    public boolean onCommand(CommandSender sender, String input) {
        if (input.startsWith("!") || input.startsWith("！")) {
            String trim = input.substring(1).trim();
            if (!trim.isEmpty()) {
                return super.onCommand(sender, trim);
            }
        }
        return false;
    }

    @CommandFormat(RELOAD_WORD_REGEX + " {what}")
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

    @CommandFormat(PLUGIN_WORD_REGEX + " {operator} {remain}")
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
                                if (pluginManager.tryLoadPlugin(sender, new com.taixue.xiaomingbot.api.bot.PluginManager.PluginLoader(file, property))) {
                                    sender.sendMessage("插件 {} 加载成功 ヽ(✿ﾟ▽ﾟ)ノ", property.getName());
                                }
                                else {
                                    sender.sendMessage("插件 {} 加载失败 (；′⌒`)", property.getName());
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        sender.sendError("小明好像无法正确读取 {} 这个文件", pluginName);
                        e.printStackTrace();
                    }
                }
                break;
            case "重载":
                if (verifyPermissionAndReport(sender, "plugin.reload")) {
                    if (pluginManager.isLoaded(pluginName)) {
                        if (pluginManager.reloadPlugin(sender, pluginName)) {
                            sender.sendMessage("插件 {} 重载成功 (๑•̀ㅂ•́)و✧", pluginName);
                        }
                        else {
                            sender.sendMessage("插件 {} 重载失败 (ﾟДﾟ*)ﾉ", pluginName);
                        }
                    }
                    else {
                        sender.sendMessage("{} 这个插件还没有加载", pluginName);
                    }
                }
                break;
            case "卸载":
                if (verifyPermissionAndReport(sender, "plugin.unload")) {
                    if (pluginManager.isLoaded(pluginName)) {
                        if (pluginManager.unloadPlugin(pluginName)) {
                            sender.sendMessage("插件 {} 卸载成功 o((>ω< ))o", pluginName);
                        }
                        else {
                            sender.sendMessage("插件 {} 卸载失败 （；´д｀）ゞ", pluginName);
                        }
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

    public void showPluginMessage(CommandSender sender, XiaomingPlugin plugin) {
        StringBuilder builder = new StringBuilder("【" + plugin.getName() + "】");
        builder.append("\n").append("版本：").append(plugin.getVersion());
        if (!plugin.getHookRecipients().isEmpty()) {
            builder.append("与这 " + plugin.getHookRecipients().size() + " 个插件挂钩：");
            for (HookHolder value : plugin.getHookRecipients().values()) {
                builder.append("\n").append(value.getSponsor().getName());
            }
        }
        sender.sendMessage(builder.toString());
    }

    @CommandFormat("授权 {who} {node}")
    @RequiredPermission("permission.user.addnode")
    public void onGiveUserPermission(CommandSender sender,
                                     @CommandParameter("who") String who,
                                     @CommandParameter("node") String node) {
        long qq = AtUtil.parseQQ(who);
        if (qq == -1) {
            sender.sendMessage("小明找不到 {} ╰（‵□′）╯", who);
            return;
        }
        XiaomingBot.getInstance().getPermissionSystem().giveUserPermission(qq, node);
        sender.sendMessage("成功授予 {} 权限节点：{} (๑•̀ㅂ•́)و✧", who, node);
    }
}
