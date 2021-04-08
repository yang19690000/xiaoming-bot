package com.taixue.xiaomingbot.api.bot;

import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;

public abstract class XiaomingBot {
    public abstract PrivateInteractorManager getPrivateInteractorManager();

    public abstract PluginManager getPluginManager();

    public abstract GroupInteractorManager getGroupInteractorManager();
}
