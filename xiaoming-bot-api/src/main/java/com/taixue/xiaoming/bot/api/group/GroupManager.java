package com.taixue.xiaoming.bot.api.group;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GroupManager extends JsonFileSavedData {
    private Map<String, Group> groups = new HashMap<>();

    public Map<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    public void addGroup(String name, Group group) {
        groups.put(name, group);
    }

    @Nullable
    public Group forGroup(final long group) {
        for (Group value : groups.values()) {
            if (value.getCode() == group) {
                return value;
            }
        }
        return null;
    }

    public boolean containsGroup(final long group) {
        return Objects.nonNull(forGroup(group));
    }

    @Nullable
    public Group forName(String name) {
        return groups.get(name);
    }

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
