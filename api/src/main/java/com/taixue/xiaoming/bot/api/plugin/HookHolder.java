package com.taixue.xiaoming.bot.api.plugin;

public interface HookHolder {
    XiaomingPlugin getSponsor();

    XiaomingPlugin getRecipient();

    XiaomingPlugin getOtherPlugin(XiaomingPlugin plugin);
}
