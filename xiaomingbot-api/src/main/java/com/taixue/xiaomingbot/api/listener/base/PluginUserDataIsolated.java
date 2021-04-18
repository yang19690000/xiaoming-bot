package com.taixue.xiaomingbot.api.listener.base;

import com.taixue.xiaomingbot.api.listener.userdata.BaseUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;

public abstract class PluginUserDataIsolated<UserData extends BaseUser>
        extends UserDataIsolated<UserData> {
    private XiaomingPlugin plugin;

    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
    }

    public XiaomingPlugin getPlugin() {
        return plugin;
    }
}
