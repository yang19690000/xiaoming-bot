package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组交互器管理器
 */
public class GroupInteractorManager {
    protected final List<GroupInteractor> interactors = new ArrayList<>();

    public void register(GroupInteractor interactor, XiaomingPlugin plugin) {
        if (!isRegistered(interactor)) {
            interactor.setPlugin(plugin);
            interactors.add(interactor);
        }
    }

    public boolean isRegistered(GroupInteractor interactor) {
        return interactors.contains(interactor);
    }

    @Nullable
    public GroupInteractor getInteractor(GroupDispatcherUserData userData) {
        for (GroupInteractor interactor: interactors) {
            if (interactor.isGroupInteractor(userData)) {
                return interactor;
            }
        }
        return null;
    }

    public void unloadPlugin(XiaomingPlugin plugin) {
        interactors.removeIf(interactor -> interactor.getPlugin() == plugin);
    }
}
