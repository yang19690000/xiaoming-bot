package com.taixue.xiaoming.bot.api.listener.interactor;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface InteractorManager extends HostObject {
    void register(Interactor interactor, XiaomingPlugin plugin);

    void registerAsCore(Interactor interactor);

    @Nullable
    Set<Interactor> getInteractors(XiaomingPlugin plugin);

    @NotNull
    Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin);

    Set<Interactor> getCoreInteractors();

    void setCoreInteractors(Set<Interactor> coreInteractors);

    Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors();

    void setPluginInteractors(Map<XiaomingPlugin, Set<Interactor>> pluginInteractors);
}
