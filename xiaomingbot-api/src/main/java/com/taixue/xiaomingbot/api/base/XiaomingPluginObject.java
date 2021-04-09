package com.taixue.xiaomingbot.api.base;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;

public abstract class XiaomingPluginObject extends XiaomingObject {
    protected XiaomingPlugin plugin;

    public XiaomingPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
    }
}
