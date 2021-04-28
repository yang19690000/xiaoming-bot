package com.taixue.xiaoming.bot.core.listener.interactor;

import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InteractorManagerImpl extends HostObjectImpl implements InteractorManager {
    public Set<Interactor> coreInteractors = new HashSet<>();

    public Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = new HashMap<>();

    @Override public void register(Interactor interactor, XiaomingPlugin plugin) {
        getOrPutInteractors(plugin).add(interactor);
        interactor.reloadInteractorDetails();
    }

    @Override public void registerAsCore(Interactor interactor) {
        coreInteractors.add(interactor);
        interactor.reloadInteractorDetails();
    }

    @Override@Nullable
    public Set<Interactor> getInteractors(XiaomingPlugin plugin) {
        return pluginInteractors.get(plugin);
    }

    @Override@NotNull
    public Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin) {
        Set<Interactor> interactors = getInteractors(plugin);
        if (Objects.isNull(interactors)) {
            interactors = new HashSet<>();
            pluginInteractors.put(plugin, interactors);
        }
        return interactors;
    }

    @Override public Set<Interactor> getCoreInteractors() {
        return coreInteractors;
    }

    @Override public void setCoreInteractors(Set<Interactor> coreInteractors) {
        this.coreInteractors = coreInteractors;
    }

    @Override public Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors() {
        return pluginInteractors;
    }

    @Override public void setPluginInteractors(Map<XiaomingPlugin, Set<Interactor>> pluginInteractors) {
        this.pluginInteractors = pluginInteractors;
    }
}
