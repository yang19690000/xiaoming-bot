package com.taixue.xiaoming.bot.api.group;

import love.forte.simbot.api.message.containers.GroupInfo;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private Set<String> tags = new HashSet<>();
    private String alias;
    private long code;
    private Set<String> blockPlugins = new HashSet<>();

    public Group() {}

    public Group(GroupInfo groupInfo) {
        setCode(groupInfo.getGroupCodeNumber());
        setAlias(groupInfo.getGroupName());
    }

    public Set<String> getBlockPlugins() {
        return blockPlugins;
    }

    public void setBlockPlugins(Set<String> blockPlugins) {
        this.blockPlugins = blockPlugins;
    }

    public void addBlockPlugin(String pluginName) {
        blockPlugins.add(pluginName);
    }

    public boolean isUnablePlugin(String pluginName) {
        return blockPlugins.contains(pluginName);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
