package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组交互器管理器
 */
public class GroupInteractorManager {
    private final List<GroupInteractor> interactors = new ArrayList<>();

    public void register(GroupInteractor interactor, XiaomingPlugin plugin) {
        if (!isRegistered(interactor)) {
            interactor.setPlugin(plugin);
            interactors.add(interactor);
        }
    }

    public List<GroupInteractor> getInteractors() {
        return interactors;
    }

    public boolean isRegistered(GroupInteractor interactor) {
        return interactors.contains(interactor);
    }

    @Nullable
    public GroupInteractor getInteractor(GroupDispatcherUser userData) {
        for (GroupInteractor interactor: interactors) {
            if (interactor.interact(userData)) {
                return interactor;
            }
        }
        return null;
    }

    public void unloadPlugin(XiaomingPlugin plugin) {
        interactors.removeIf(interactor -> interactor.getPlugin() == plugin);
    }
}
