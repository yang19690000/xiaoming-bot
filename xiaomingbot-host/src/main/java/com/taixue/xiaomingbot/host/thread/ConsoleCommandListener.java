package com.taixue.xiaomingbot.host.thread;

import com.taixue.xiaomingbot.api.command.CommandExecutor;
import com.taixue.xiaomingbot.api.command.CommandFormat;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.listener.base.UserDataIsolator;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.host.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ConsoleCommandListener extends CommandExecutor implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandFormat("stop")
    public void onStop(CommandSender sender) {
        XiaomingBot xiaomingBot = XiaomingBot.getInstance();
        sender.sendMessage("开始关闭服务器");
        sender.sendMessage("卸载所有插件");
        unloadAllPlugins(sender);
        sender.sendMessage("插件卸载完成");

        sender.sendMessage("保存权限系统文件");
        xiaomingBot.getPermissionSystem().save();

        sender.sendMessage("卸载指令处理器");
        xiaomingBot.getCommandManager().unloadAll();

        sender.sendMessage("卸载群组交互器");
        GroupInteractorManager groupInteractorManager = xiaomingBot.getGroupInteractorManager();
        for (GroupInteractor interactor : groupInteractorManager.getInteractors()) {
            try {
                sender.sendMessage("\t正在卸载 {}（加载自插件 {}）", interactor.getClass(), interactor.getPlugin().getName());
                unloadGroupInteactor(sender, interactor);
            }
            catch (Exception exception) {

            }
        }

        System.exit(0);
    }

    public void unloadGroupInteactor(CommandSender sender, GroupInteractor interactor) {
        UserDataIsolator userDataIsolator = interactor.getUserDataIsolator();
        if (userDataIsolator.getValue().isEmpty()) {
            sender.sendMessage("\t\t该交互器没有和任何用户交互");
        }
        else {
            sender.sendMessage("\t\t该交互器正在和 {} 名用户交互：", userDataIsolator.getValue().size());
            Set<GroupInteractorUser> userData = (Set<GroupInteractorUser>) (Object) userDataIsolator.getValue().keySet();
            for (GroupInteractorUser userDatum : userData) {
                sender.sendMessage("\t\t\t所在群：{}\n", userDatum.getGroup());
                sender.sendMessage("\t\t\tQQ：{}\n", userDatum.getQQ());
                sender.sendMessage("\t\t\t最后输入：{}\n", userDatum.getMessage());
                try {
                    interactor.onUserOut(userDatum.getQQ());
                }
                catch (Exception exception) {

                }
            }
        }
    }

    public void unloadAllPlugins(CommandSender sender) {
        Map<String, XiaomingPlugin> loadedPlugins = XiaomingBot.getInstance().getPluginManager().getLoadedPlugins();
        for (XiaomingPlugin value : loadedPlugins.values()) {
            try {
                sender.sendMessage("正在卸载插件 {}", value.getName());
                value.onDisable();
                sender.sendMessage("插件 {} 卸载完成", value.getName());
            }
            catch (Exception exception) {
                sender.sendError("卸载 {} 卸载时出现异常：{}", value.getName(), exception);
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        CommandSender consoleCommandSender = XiaomingBot.getInstance().getConsoleCommandSender();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                try {
                    if (!onCommand(consoleCommandSender, input) &&
                        !XiaomingBot.getInstance().getCommandManager().execute(consoleCommandSender, input)) {
                        logger.error("小明不知道这是什么指令呢 (；′⌒`)");
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
