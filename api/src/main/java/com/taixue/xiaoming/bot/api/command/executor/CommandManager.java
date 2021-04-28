package com.taixue.xiaoming.bot.api.command.executor;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;

import java.util.Map;
import java.util.Set;

public interface CommandManager extends XiaomingObject {
    Set<CommandExecutor> getCoreCommandExecutors();

    Map<XiaomingPlugin, Set<CommandExecutor>> getPluginCommandExecutors();

    void onUnknownCommand(DispatcherUser sender, String input);

    Set<CommandExecutor> getPluginCommandExecutors(XiaomingPlugin plugin);

    Set<CommandExecutor> getOrPutCommandExecutors(XiaomingPlugin plugin);

    void register(CommandExecutor executor,
                  XiaomingPlugin plugin);

    void registerAsCore(CommandExecutor executor);
}
