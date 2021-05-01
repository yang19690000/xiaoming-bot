package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountCommandExecutor extends CommandExecutorImpl {
    private final AccountManager accountManager = getXiaomingBot().getAccountManager();

    @NotNull
    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.ACCOUNT_REGEX;
    }

    @Command(CommandWordUtil.ACCOUNT_REGEX + " {qq}")
    @RequirePermission("account.look")
    public void onLookAccount(@NotNull final XiaomingUser user,
                              @CommandParameter("qq") final long qq) {
        final Account account = accountManager.getAccount(qq);
        if (Objects.isNull(account)) {
            user.sendError("该用户没有任何信息");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(account.getAlias()).append("（").append(qq).append("）").append("：")
                    .append("详情：").append(account.getProperties());
            if (user instanceof QQXiaomingUser) {
                if (user instanceof GroupXiaomingUser) {
                    user.sendMessage("该用户的相关信息已经私发你啦，记得查收");
                }
                ((QQXiaomingUser) user).sendPrivateMessage(builder.toString());
            } else {
                user.sendMessage(builder.toString());
            }
        }
    }

    @Command(CommandWordUtil.ACCOUNT_REGEX + " " + CommandWordUtil.UNBLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.unblock")
    public void onUnblockPlugin(@NotNull final GroupXiaomingUser user,
                                @NotNull @CommandParameter("plugin") final String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
        if (account.isBlockPlugin(plugin)) {
            account.getBlockPlugins().remove(plugin);
            account.readySave();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功取消屏蔽了插件{}", plugin);
            } else {
                user.sendWarning("成功取消屏蔽了插件{}，但这个插件还没有加载", plugin);
            }
        } else {
            user.sendError("你已经屏蔽了插件{}", plugin);
        }
    }

    @Command(CommandWordUtil.ACCOUNT_REGEX + " " + CommandWordUtil.BLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.block")
    public void onBlockPlugin(@NotNull final QQXiaomingUser user,
                              @NotNull @CommandParameter("plugin") final String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
        if (account.isBlockPlugin(plugin)) {
            user.sendMessage("你已经屏蔽了插件{}", plugin);
        } else {
            account.addBlockPlugin(plugin);
            account.readySave();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功屏蔽了插件{}", plugin);
            } else {
                user.sendWarning("成功屏蔽了插件{}，但这个插件还没有加载", plugin);
            }
        }
    }
}
