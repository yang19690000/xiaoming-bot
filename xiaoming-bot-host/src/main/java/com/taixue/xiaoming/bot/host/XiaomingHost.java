package com.taixue.xiaoming.bot.host;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.exception.MultipleHostException;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.host.command.executor.*;
import com.taixue.xiaoming.bot.host.runnable.ConsoleCommandRunnable;
import com.taixue.xiaoming.bot.util.PathUtil;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SimbotApplication
public class XiaomingHost extends HostObject implements SimbotProcess {
    private static XiaomingHost INSTANCE;

    public static XiaomingHost getInstance() {
        return INSTANCE;
    }

    public XiaomingHost() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = this;
        } else {
            throw new MultipleHostException();
        }
    }

    private final ConsoleDispatcherUser consoleXiaomingUser = new ConsoleDispatcherUser();

    public ConsoleDispatcherUser getConsoleXiaomingUser() {
        return consoleXiaomingUser;
    }

    private final CoreCommandExecutor coreCommandExecutor = new CoreCommandExecutor();
    private final GroupCommandExecutor groupCommandExecutor = new GroupCommandExecutor();
    private final EmojiCommandExecutor emojiCommandExecutor = new EmojiCommandExecutor();
    private final PermissionCommandExecutor permissionCommandExecutor = new PermissionCommandExecutor();
    private final ConsoleCommandRunnable consoleCommandRunnable = new ConsoleCommandRunnable();
    private final AccountCommandExecutor accountCommandExecutor = new AccountCommandExecutor();
    private final CallLimitCommandExecutor callLimitCommandExecutor = new CallLimitCommandExecutor();

    @Override
    public void post(@NotNull SimbotContext context) {
        final XiaomingBot xiaomingBot = getXiaomingBot();
        final BotSender sender = context.getBotManager().getDefaultBot().getSender();

        // 注册指令处理器
        final CommandManager commandManager = xiaomingBot.getCommandManager();
        commandManager.registerAsCore(coreCommandExecutor);
        commandManager.registerAsCore(groupCommandExecutor);
        commandManager.registerAsCore(emojiCommandExecutor);
        commandManager.registerAsCore(permissionCommandExecutor);
        commandManager.registerAsCore(consoleCommandRunnable);
        commandManager.registerAsCore(accountCommandExecutor);
        commandManager.registerAsCore(callLimitCommandExecutor);

        xiaomingBot.getPluginManager().loadAllPlugins(consoleXiaomingUser);

        // 启动控制台线程
        xiaomingBot.execute(consoleCommandRunnable);

        for (Group log : xiaomingBot.getGroupManager().forTag("log")) {
            sender.SENDER.sendGroupMsg(log.getCode(), "小明正常启动" + getXiaomingBot().getEmojiManager().get("happy"));
        }
    }

    @Override
    public void pre(@NotNull Configuration config) {

    }

    public static void main(String[] args) {
        PathUtil.CONFIG_DIR.mkdirs();
        PathUtil.PLUGIN_DIR.mkdirs();
        PathUtil.ACCOUNT_DIR.mkdirs();
        SimbotApp.run(XiaomingHost.class, args);
    }
}
