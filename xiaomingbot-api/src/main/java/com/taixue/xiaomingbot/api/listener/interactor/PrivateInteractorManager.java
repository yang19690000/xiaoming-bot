package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrivateInteractorManager {
    private final List<PrivateInteractor> interactors = new ArrayList<>();

    public void register(PrivateInteractor interactor, XiaomingPlugin plugin) {
        if (!isRegistered(interactor)) {
            interactor.setPlugin(plugin);
            interactors.add(interactor);
        }
    }

    public boolean isRegistered(PrivateInteractor interactor) {
        return interactors.contains(interactor);
    }

    @Nullable
    public PrivateInteractor getInteractor(PrivateDispatcherUser userData) {
        for (PrivateInteractor interactor: interactors) {
            if (interactor.interact(userData)) {
                return interactor;
            }
        }
        return null;
    }

    public List<PrivateInteractor> getInteractors() {
        return interactors;
    }

    public void unloadPlugin(XiaomingPlugin plugin) {
        interactors.removeIf(interactor -> interactor.getPlugin() == plugin);
    }
}
