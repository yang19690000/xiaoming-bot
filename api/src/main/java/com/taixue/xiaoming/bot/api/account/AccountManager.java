package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface AccountManager extends XiaomingObject {
    @Nullable
    Account getAccount(final long qq);

    @NotNull
    Account getOrPutAccount(final long qq,
                            final String alias);

    @NotNull
    Account getOrPutAccount(final long qq);

    @NotNull
    File getDirectory();

    @NotNull
    Account loadAccount(final File file);
}
