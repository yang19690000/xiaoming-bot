package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Chuanwise
 */
public class AccountManager extends HostObject {
    private final File directory;
    private Set<Account> accounts = new HashSet<>();

    public AccountManager(final File directory) {
        this.directory = directory;
    }

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

    @NotNull
    public Account getOrPutAccount(final long qq,
                                   final String alias) {
        Account account = getAccount(qq);
        if (Objects.isNull(account)) {
            account = new Account();
            account.setQq(qq);
            account.setAlias(alias);
            account.setFile(new File(directory, qq + ".json"));
            accounts.add(account);
        }
        return account;
    }

    @NotNull
    public Account getOrPutAccount(final long qq) {
        return getOrPutAccount(qq, null);
    }

    public File getDirectory() {
        return directory;
    }

    public Account loadAccount(File file) {
        try {
            return getXiaomingBot().getJsonFileSavedDataFactory().forFile(file, Account.class);
        } catch (Exception exception) {
            getLogger().error("载入用户数据文件 {} 时出现异常：{}", file, exception);
            exception.printStackTrace();
            return null;
        }
    }
}
