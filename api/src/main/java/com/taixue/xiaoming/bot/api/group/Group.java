package com.taixue.xiaoming.bot.api.group;

import java.util.Set;

public interface Group {
    Set<String> getBlockPlugins();

    void setBlockPlugins(Set<String> blockPlugins);

    void addBlockPlugin(String pluginName);

    boolean isUnablePlugin(String pluginName);

    void setAlias(String alias);

    String getAlias();

    boolean hasTag(String tag);

    Set<String> getTags();

    void setTags(Set<String> tags);

    long getCode();

    void setCode(long code);
}
