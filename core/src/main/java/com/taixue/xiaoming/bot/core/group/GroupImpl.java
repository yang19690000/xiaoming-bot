package com.taixue.xiaoming.bot.core.group;

import com.taixue.xiaoming.bot.api.group.Group;
import love.forte.simbot.api.message.containers.GroupInfo;

import java.util.HashSet;
import java.util.Set;

public class GroupImpl implements Group {
    private Set<String> tags = new HashSet<>();
    private String alias;
    private long code;
    private Set<String> blockPlugins = new HashSet<>();

    public GroupImpl() {
    }

    public GroupImpl(GroupInfo groupInfo) {
        setCode(groupInfo.getGroupCodeNumber());
        setAlias(groupInfo.getGroupName());
    }

    @Override
    public Set<String> getBlockPlugins() {
        return blockPlugins;
    }

    @Override
    public void setBlockPlugins(Set<String> blockPlugins) {
        this.blockPlugins = blockPlugins;
    }

    @Override
    public void addBlockPlugin(String pluginName) {
        blockPlugins.add(pluginName);
    }

    @Override
    public boolean isUnablePlugin(String pluginName) {
        return blockPlugins.contains(pluginName);
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public void setCode(long code) {
        this.code = code;
    }
}
