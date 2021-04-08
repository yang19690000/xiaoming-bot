package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUserData;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrivateInteractorManager {
    protected final List<PrivateInteractor> interactors = new ArrayList<>();

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
    public PrivateInteractor getInteractor(PrivateDispatcherUserData userData) {
        for (PrivateInteractor interactor: interactors) {
            if (interactor.isInteractor(userData)) {
                return interactor;
            }
        }
        return null;
    }
}
