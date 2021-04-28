package com.taixue.xiaoming.bot.api.group;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface GroupManager extends FileSavedData {
    Map<String, Group> getGroups();

    void setGroups(Map<String, Group> groups);

    void addGroup(String name, Group group);

    @Nullable
    Group forGroup(long group);

    boolean containsGroup(long group);

    @Nullable
    Group forName(String name);

    @NotNull
    Set<Group> forTag(String tag);
}
