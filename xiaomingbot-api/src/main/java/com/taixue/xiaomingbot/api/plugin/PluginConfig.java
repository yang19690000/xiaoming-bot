package com.taixue.xiaomingbot.api.plugin;

import com.alibaba.fastjson.JSON;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class PluginConfig {
    private transient File file;
    private Map<String, List<Long>> unableGroups;
    private Map<String, List<Long>> unableUsers;

    public void setUnableUsers(Map<String, List<Long>> unableUsers) {
        this.unableUsers = unableUsers;
    }

    public Map<String, List<Long>> getUnableUsers() {
        return unableUsers;
    }

    public void setUnableGroups(Map<String, List<Long>> unableGroups) {
        this.unableGroups = unableGroups;
    }

    public Map<String, List<Long>> getUnableGroups() {
        return unableGroups;
    }

    public static PluginConfig forFile(File file) {
        PluginConfig config = null;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                config = JSON.parseObject(fileInputStream, PluginConfig.class);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Objects.isNull(config)) {
            config = new PluginConfig();
        }
        if (Objects.isNull(config.unableGroups)) {
            config.unableGroups = new HashMap<>();
        }
        if (Objects.isNull(config.unableUsers)) {
            config.unableUsers = new HashMap<>();
        }
        config.file = file;
        return config;
    }

    public void save() {
        try {
            if (!file.exists() || file.isDirectory()) {
                file.createNewFile();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(JSON.toJSONString(this).getBytes());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public List<Long> unableInGroup(String pluginName) {
        return unableGroups.get(pluginName);
    }

    @Nullable
    public List<Long> unableInUser(String pluginName) {
        return unableUsers.get(pluginName);
    }

    public boolean unableInGroup(String pluginName, long group) {
        List<Long> longs = unableInGroup(pluginName);
        if (Objects.isNull(longs)) {
            return false;
        }
        return longs.contains(group);
    }

    public boolean unableInUser(String pluginName, long qq) {
        List<Long> longs = unableInGroup(pluginName);
        if (Objects.isNull(longs)) {
            return false;
        }
        return longs.contains(qq);
    }

    public void toUnableInGroup(String pluginName, long group) {
        if (!unableGroups.containsKey(pluginName)) {
            unableGroups.put(pluginName, new ArrayList<>());
        }
        unableGroups.get(pluginName).add(group);
        save();
    }

    public void toUnableInUser(String pluginName, long qq) {
        if (!unableUsers.containsKey(pluginName)) {
            unableUsers.put(pluginName, new ArrayList<>());
        }
        unableUsers.get(pluginName).add(qq);
        save();
    }

    public void enableInGroup(String pluginName, long group) {
        List<Long> longs = unableGroups.get(pluginName);
        if (Objects.nonNull(longs) && longs.contains(group)) {
            longs.remove(group);
        }
        if (longs.isEmpty()) {
            unableGroups.remove(pluginName);
        }
        save();
    }

    public void enableInUser(String pluginName, long qq) {
        List<Long> longs = unableUsers.get(pluginName);
        if (Objects.nonNull(longs) && longs.contains(qq)) {
            longs.remove(qq);
        }
        if (longs.isEmpty()) {
            unableUsers.remove(pluginName);
        }
        save();
    }
}
