package com.taixue.xiaomingbot.api.bot;

import com.taixue.xiaomingbot.api.base.XiaomingConfig;
import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.PermissionSystem;
import com.taixue.xiaomingbot.api.picture.EmojiManager;
import com.taixue.xiaomingbot.api.picture.PictureManager;
import com.taixue.xiaomingbot.api.plugin.PluginConfig;
import com.taixue.xiaomingbot.api.plugin.PluginManager;
import com.taixue.xiaomingbot.api.timetask.TimeTaskManager;
import com.taixue.xiaomingbot.api.user.UserManager;
import com.taixue.xiaomingbot.util.MessageManager;

public abstract class XiaomingBot {
    public abstract PrivateInteractorManager getPrivateInteractorManager();

    public abstract PluginManager getPluginManager();

    public abstract GroupInteractorManager getGroupInteractorManager();

    public abstract CommandManager getCommandManager();

    public abstract PermissionSystem getPermissionSystem();

    public abstract EmojiManager getEmojis();

    public abstract GroupManager getGroupManager();

    public abstract PictureManager getPictureManager();

    public abstract PluginConfig getPluginConfig();

    public abstract MessageManager getMessageManager();

    public abstract XiaomingConfig getXiaomingConfig();

    public abstract UserManager getUserManager();

    public abstract TimeTaskManager getTimeTaskManager();

    public abstract void execute(Thread thread);

    public abstract void execute(Runnable runnable);
}
