package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AccountCommandExecutor extends CommandExecutor {
    private final AccountManager accountManager = getXiaomingBot().getAccountManager();

    @NotNull
    @Override
    public String getHelpPrefix() {
        return CommandWordUtil.ACCOUNT_REGEX;
    }

    @CommandFormat(CommandWordUtil.ACCOUNT_REGEX + " {qq}")
    @RequiredPermission("account.look")
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

    @CommandFormat(CommandWordUtil.ACCOUNT_REGEX + " " + CommandWordUtil.UNBLOCK_REGEX + " {plugin}")
    @RequiredPermission("account.plugin.unblock")
    public void onUnblockPlugin(@NotNull final GroupXiaomingUser user,
                                @NotNull @CommandParameter("plugin") final String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
        if (account.isBlockPlugin(plugin)) {
            account.getBlockPlugins().remove(plugin);
            account.save();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功取消屏蔽了插件{}", plugin);
            } else {
                user.sendWarning("成功取消屏蔽了插件{}，但这个插件还没有加载", plugin);
            }
        } else {
            user.sendError("你已经屏蔽了插件{}", plugin);
        }
    }

    @CommandFormat(CommandWordUtil.ACCOUNT_REGEX + " " + CommandWordUtil.BLOCK_REGEX + " {plugin}")
    @RequiredPermission("account.plugin.block")
    public void onBlockPlugin(@NotNull final QQXiaomingUser user,
                              @NotNull @CommandParameter("plugin") final String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
        if (account.isBlockPlugin(plugin)) {
            user.sendMessage("你已经屏蔽了插件{}", plugin);
        } else {
            account.addBlockPlugin(plugin);
            account.save();
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功屏蔽了插件{}", plugin);
            } else {
                user.sendWarning("成功屏蔽了插件{}，但这个插件还没有加载", plugin);
            }
        }
    }
}
