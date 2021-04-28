package com.taixue.xiaoming.bot.core.base;

import com.taixue.xiaoming.bot.api.base.PluginObject;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PluginObjectImpl implements PluginObject {
    private XiaomingPlugin plugin;

    @Override
    @Nullable
    public XiaomingPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(@NotNull final XiaomingPlugin plugin) {
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
