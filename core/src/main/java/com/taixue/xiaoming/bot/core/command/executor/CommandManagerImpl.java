package com.taixue.xiaoming.bot.core.command.executor;

import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;

import java.util.*;

public class CommandManagerImpl extends HostObjectImpl implements CommandManager {
    private Set<CommandExecutor> coreCommandExecutors = new HashSet<>();
    private Map<XiaomingPlugin, Set<CommandExecutor>> pluginCommandExecutors = new HashMap<>();

    @Override
    public Set<CommandExecutor> getCoreCommandExecutors() {
        return coreCommandExecutors;
    }

    @Override
    public Map<XiaomingPlugin, Set<CommandExecutor>> getPluginCommandExecutors() {
        return pluginCommandExecutors;
    }

    @Override
    public void onUnknownCommand(final DispatcherUser sender, final String input) {
    }

    @Override
    public Set<CommandExecutor> getPluginCommandExecutors(final XiaomingPlugin plugin) {
        return pluginCommandExecutors.get(plugin);
    }

    @Override
    public Set<CommandExecutor> getOrPutCommandExecutors(final XiaomingPlugin plugin) {
        Set<CommandExecutor> result = getPluginCommandExecutors(plugin);
        if (Objects.isNull(result)) {
            final HashSet<CommandExecutor> set = new HashSet<>();
            pluginCommandExecutors.put(plugin, set);
            return set;
        } else {
            return result;
        }
    }

    @Override
    public void register(final CommandExecutor executor,
                         final XiaomingPlugin plugin) {
        getLogger().info("注册来自 {} 插件的指令处理器：{}", plugin.getCompleteName(), executor.getClass().getName());
        executor.setPlugin(plugin);
        executor.reloadSubcommandExecutor();
        getOrPutCommandExecutors(plugin).add(executor);
    }

    @Override
    public void registerAsCore(final CommandExecutor executor) {
        getLogger().info("以内核形式注册指令处理器：{}", executor.getClass().getName());
        executor.reloadSubcommandExecutor();
        coreCommandExecutors.add(executor);
    }
}