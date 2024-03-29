package com.taixue.xiaoming.bot.host.runnable;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.core.user.XiaomingUserImpl;
import com.taixue.xiaoming.bot.host.XiaomingLauncher;
import com.taixue.xiaoming.bot.util.AtUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;
import java.util.Set;

public class ConsoleCommandRunnable extends CommandExecutorImpl implements Runnable {
    private final String COMMAND_HEAD_REGEX = "(控制台|console|后台)";

    @Command("stop")
    public void onStop(final XiaomingUser user) {
        XiaomingLauncher.getInstance().close(user);
    }
/*
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
    */

    @Override
    public String getHelpPrefix() {
        return COMMAND_HEAD_REGEX;
    }

    @Command(COMMAND_HEAD_REGEX + " qq {qq}")
    @RequirePermission("console.qq")
    public void onSetConsoleQQ(@NotNull final XiaomingUserImpl user,
                               @CommandParameter("qq") final String qqString) {
        long qq = AtUtil.parseQQ(qqString);
        if (qq == -1) {
            user.sendError("{} 似乎不是一个正确的 QQ 哦", qqString);
        }

        XiaomingLauncher.getInstance().getConsoleXiaomingUser().setQQ(qq);
        user.sendMessage("已设置控制台执行身份为 QQ：{}", qqString);
    }

    @Override
    public void run() {
        final CommandManager commandManager = getXiaomingBot().getCommandManager();
        final ConsoleDispatcherUser consoleXiaomingUser = XiaomingLauncher.getInstance().getConsoleXiaomingUser();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                consoleXiaomingUser.setMessage(input);
                try {
                    boolean executed = false;
                    for (CommandExecutor coreCommandExecutor : commandManager.getCoreCommandExecutors()) {
                        if (coreCommandExecutor.onCommand(consoleXiaomingUser)) {
                            executed = true;
                            break;
                        }
                    }
                    for (Set<CommandExecutor> value : commandManager.getPluginCommandExecutors().values()) {
                        for (CommandExecutor commandExecutor : value) {
                            if (commandExecutor.onCommand(consoleXiaomingUser)) {
                                executed = true;
                                break;
                            }
                        }
                    }
                    if (!executed) {
                        getLogger().error("小明不知道你的意思 (；′⌒`)");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}