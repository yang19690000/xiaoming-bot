package com.taixue.xiaoming.bot.core.account;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import love.forte.simbot.api.message.results.FriendInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Chuanwise
 */
public class AccountManagerImpl extends HostObjectImpl implements AccountManager {
    private final File directory;
    private Set<Account> accounts = new HashSet<>();

    public AccountManagerImpl(final File directory) {
        this.directory = directory;
    }

    @Override
    @Nullable
    public Account getAccount(final long qq) {
        for (Account account : accounts) {
            if (account.getQq() == qq) {
                return account;
            }
        }
        File file = new File(directory, qq + ".json");
        return file.isFile() ? loadAccount(file) : null;
    }

    @Override
    @NotNull
    public Account getOrPutAccount(final long qq,
                                   final String alias) {
        Account account = getAccount(qq);
        if (Objects.isNull(account)) {
            account = new AccountImpl();
            account.setQq(qq);
            account.setAlias(alias);
            account.setFile(new File(directory, qq + ".json"));
            accounts.add(account);
        }
        return account;
    }

    @Override
    @NotNull
    public Account getOrPutAccount(final long qq) {
        final MsgSender msgSender = getXiaomingBot().getMsgSender();
        try {
            final FriendInfo friendInfo = msgSender.GETTER.getFriendInfo(qq);
            return getOrPutAccount(qq, friendInfo.getAccountRemarkOrNickname());
        } catch (Exception exception) {
            return getOrPutAccount(qq, null);
        }
    }

    @Override
    @NotNull
    public File getDirectory() {
        return directory;
    }

    @Override
    @NotNull
    public Account loadAccount(final File file) {
        try {
            return getXiaomingBot().getFileSavedDataFactory().forFile(file, Account.class);
        } catch (Exception exception) {
            getLogger().error("载入用户数据文件 {} 时出现异常：{}", file, exception);
            exception.printStackTrace();
            return null;
        }
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }
}