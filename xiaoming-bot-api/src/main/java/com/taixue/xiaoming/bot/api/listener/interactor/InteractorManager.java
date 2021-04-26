package com.taixue.xiaoming.bot.api.listener.interactor;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InteractorManager {
    public Set<Interactor> coreInteractors = new HashSet<>();

    public Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = new HashMap<>();

    public void register(Interactor interactor, XiaomingPlugin plugin) {
        getOrPutInteractors(plugin).add(interactor);
        interactor.reloadInteractorDetails();
    }

    public void registerAsCore(Interactor interactor) {
        coreInteractors.add(interactor);
        interactor.reloadInteractorDetails();
    }

    @Nullable
    public Set<Interactor> getInteractors(XiaomingPlugin plugin) {
        return pluginInteractors.get(plugin);
    }

    @NotNull
    public Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin) {
        Set<Interactor> interactors = getInteractors(plugin);
        if (Objects.isNull(interactors)) {
            interactors = new HashSet<>();
            pluginInteractors.put(plugin, interactors);
        }
        return interactors;
    }

    public Set<Interactor> getCoreInteractors() {
        return coreInteractors;
    }

    public void setCoreInteractors(Set<Interactor> coreInteractors) {
        this.coreInteractors = coreInteractors;
    }

    public Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors() {
        return pluginInteractors;
    }

    public void setPluginInteractors(Map<XiaomingPlugin, Set<Interactor>> pluginInteractors) {
        this.pluginInteractors = pluginInteractors;
    }
}
