package com.taixue.xiaoming.bot.api.command.executor;

import com.taixue.xiaoming.bot.api.base.PluginObject;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

public interface CommandExecutor extends PluginObject {
    Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");

    void reloadSubcommandExecutor();

    boolean verifyPermissionAndReport(@NotNull XiaomingUser sender,
                                      @NotNull String node);

    void tellLackPermission(@NotNull XiaomingUser sender,
                            @NotNull String node);

    @NotNull
    String getCommandPrefix();

    boolean onCommand(@NotNull DispatcherUser user) throws Exception;

    @Nullable
    Object onParameter(@NotNull Parameter parameter);

    @Nullable
    Object onParameter(@NotNull DispatcherUser user,
                       @NotNull Class<?> clazz,
                       @NotNull String parameterName,
                       @Nullable String value);

    @NotNull
    String getHelpPrefix();

    Set<String> getUsageStrings(@NotNull XiaomingUser user);

    void onHelp(@NotNull XiaomingUser user);

}
