package com.taixue.xiaoming.bot.api.base;

import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PluginObject extends XiaomingObject {
    @Nullable
    XiaomingPlugin getPlugin();

    void setPlugin(@NotNull XiaomingPlugin plugin);
}
