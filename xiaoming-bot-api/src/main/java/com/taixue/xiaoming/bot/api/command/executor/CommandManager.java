package com.taixue.xiaoming.bot.api.command.executor;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;

import java.util.*;

public class CommandManager extends HostObject {
    private Set<CommandExecutor> coreCommandExecutors = new HashSet<>();
    private Map<XiaomingPlugin, Set<CommandExecutor>> pluginCommandExecutors = new HashMap<>();

    public Set<CommandExecutor> getCoreCommandExecutors() {
        return coreCommandExecutors;
    }

    public Map<XiaomingPlugin, Set<CommandExecutor>> getPluginCommandExecutors() {
        return pluginCommandExecutors;
    }

    public void onUnknownCommand(final DispatcherUser sender, final String input) {}

    public Set<CommandExecutor> getPluginCommandExecutors(XiaomingPlugin plugin) {
        return pluginCommandExecutors.get(plugin);
    }

    public Set<CommandExecutor> getOrPutCommandExecutors(XiaomingPlugin plugin) {
        Set<CommandExecutor> result = getPluginCommandExecutors(plugin);
        if (Objects.isNull(result)) {
            final HashSet<CommandExecutor> set = new HashSet<>();
            pluginCommandExecutors.put(plugin, set);
            return set;
        }
        else {
            return result;
        }
    }

    public void register(final CommandExecutor executor,
                         final  XiaomingPlugin plugin) {
        getLogger().info("注册来自 {} 插件的指令处理器：{}", plugin.getCompleteName(), executor.getClass().getName());
        executor.setPlugin(plugin);
        executor.reloadSubcommandExecutor();
        getOrPutCommandExecutors(plugin).add(executor);
    }

    public void registerAsCore(final CommandExecutor executor) {
        getLogger().info("以内核形式注册指令处理器：{}", executor.getClass().getName());
        executor.reloadSubcommandExecutor();
        coreCommandExecutors.add(executor);
    }
}
