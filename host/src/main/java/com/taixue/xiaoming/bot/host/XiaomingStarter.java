package com.taixue.xiaoming.bot.host;

import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.bot.XiaomingBotImpl;
import com.taixue.xiaoming.bot.core.listener.dispatcher.user.ConsoleDispatcherUserImpl;
import com.taixue.xiaoming.bot.host.command.executor.*;
import com.taixue.xiaoming.bot.host.runnable.ConsoleCommandRunnable;
import com.taixue.xiaoming.bot.util.PathUtil;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.bot.BotRegisterInfo;
import love.forte.simbot.bot.NoSuchBotException;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

@SimbotApplication
public class XiaomingStarter extends HostObjectImpl implements SimbotProcess {
    public static final XiaomingBot XIAOMING_BOT = new XiaomingBotImpl();
    public static final XiaomingStarter INSTANCE = new XiaomingStarter();

    public static XiaomingStarter getInstance() {
        return INSTANCE;
    }

    private final ConsoleDispatcherUser consoleXiaomingUser = new ConsoleDispatcherUserImpl();

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
        // 登录 Bot 账号
        final Config config = getXiaomingBot().getConfig();

        if (!config.getFile().exists()) {
            config.save();
            getLogger().error("请打开 {} 并设置机器人账号密码", config.getFile().getAbsolutePath());
            return;
        }
        for (BotAccount account : config.getAccounts()) {
            try {
                context.getBotManager().registerBot(new BotRegisterInfo(String.valueOf(account.getQq()), account.getPassword()));
            } catch (Exception exception) {
                getLogger().error("登录账号：{} 时出现异常：{}", account.getQq(), exception);
                exception.printStackTrace();
            }
        }

        try {
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
            getLogger().info("小明正常启动" + getXiaomingBot().getEmojiManager().get("happy"));
        } catch (NoSuchBotException exception) {
            getLogger().error("没有任何一个可以登录的机器人 QQ 账号，请打开 {} 并核对机器人账号密码是否正确", config.getFile().getAbsolutePath());
        }
    }

    @Override
    public void pre(@NotNull Configuration config) {
    }

    public static void main(String[] args) {
        PathUtil.CONFIG_DIR.mkdirs();
        PathUtil.PLUGIN_DIR.mkdirs();
        PathUtil.ACCOUNT_DIR.mkdirs();
        SimbotApp.run(XiaomingStarter.class, args);
    }
}