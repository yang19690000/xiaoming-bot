package com.taixue.xiaomingbot.api.bot;

import com.taixue.xiaomingbot.api.base.XiaomingObject;
import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.BasePermissionSystem;
import com.taixue.xiaomingbot.util.Emojis;

public abstract class XiaomingBot {
    public abstract PrivateInteractorManager getPrivateInteractorManager();

    public abstract PluginManager getPluginManager();

    public abstract GroupInteractorManager getGroupInteractorManager();

    public abstract CommandManager getCommandManager();

    public abstract BasePermissionSystem getPermissionSystem();

    public abstract Emojis getEmojis();
}
