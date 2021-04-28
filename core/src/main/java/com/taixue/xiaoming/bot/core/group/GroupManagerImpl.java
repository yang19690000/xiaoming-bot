package com.taixue.xiaoming.bot.core.group;

import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GroupManagerImpl extends JsonFileSavedData implements GroupManager {
    private Map<String, Group> groups = new HashMap<>();

    @Override
    public Map<String, Group> getGroups() {
        return groups;
    }

    @Override
    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    @Override
    public void addGroup(String name, Group group) {
        groups.put(name, group);
    }

    @Override
    @Nullable
    public Group forGroup(final long group) {
        for (Group value : groups.values()) {
            if (value.getCode() == group) {
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean containsGroup(final long group) {
        return Objects.nonNull(forGroup(group));
    }

    @Override
    @Nullable
    public Group forName(String name) {
        return groups.get(name);
    }

    @Override
    @NotNull
    public Set<Group> forTag(String tag) {
        Set<Group> result = new HashSet<>();
        for (Group value : groups.values()) {
            if (value.hasTag(tag)) {
                result.add(value);
            }
        }
        return result;
    }
}