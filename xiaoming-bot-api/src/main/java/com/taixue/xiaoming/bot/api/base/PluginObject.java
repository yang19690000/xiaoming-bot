package com.taixue.xiaoming.bot.api.base;

import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PluginObject implements XiaomingObject {
    private XiaomingPlugin plugin;

    public XiaomingPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(final XiaomingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        if (Objects.isNull(plugin)) {
            return LoggerFactory.getLogger(getClass());
        }
        else {
            return plugin.getLogger();
        }
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return XiaomingBot.getInstance();
    }
}
