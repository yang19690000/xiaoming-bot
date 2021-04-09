package com.taixue.xiaomingbot.api.bot;

import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.PermissionSystem;
import com.taixue.xiaomingbot.util.EmojiManager;

public abstract class XiaomingBot {
    public abstract PrivateInteractorManager getPrivateInteractorManager();

    public abstract PluginManager getPluginManager();

    public abstract GroupInteractorManager getGroupInteractorManager();

    public abstract CommandManager getCommandManager();

    public abstract PermissionSystem getPermissionSystem();

    public abstract EmojiManager getEmojis();

    public abstract GroupManager getGroupManager();
}
