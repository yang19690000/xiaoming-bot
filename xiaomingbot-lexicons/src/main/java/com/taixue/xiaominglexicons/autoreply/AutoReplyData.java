package com.taixue.xiaominglexicons.autoreply;

import com.alibaba.fastjson.JSON;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class AutoReplyData {
    protected transient File file;

    protected Map<String, Map<String, AutoReplyItem>> users;
    protected Map<String, AutoReplyItem> global;
    protected Map<String, Map<String, AutoReplyItem>> group;
    protected List<String> illegalKeyRegex, illegalValueRegex;

    public static AutoReplyData forFile(File file) throws IOException {
        AutoReplyData data = null;
        if (file.exists() && !file.isDirectory()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                data = JSON.parseObject(fileInputStream, AutoReplyData.class);
            }
        }
        return data;
    }

    public static AutoReplyData forFileOrNew(File file) {
        AutoReplyData data = null;
        try {
            data = forFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.isNull(data)) {
            data = new AutoReplyData();
            data.file = file;
            data.users = new HashMap<>();
            data.global = new HashMap<>();
            data.group = new HashMap<>();
            try {
                data.save();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void save() throws IOException {
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(JSON.toJSONString(this).getBytes());
        }
    }

    public List<String> getIllegalKeyRegex() {
        return illegalKeyRegex;
    }

    public void setIllegalKeyRegex(List<String> illegalKeyRegex) {
        this.illegalKeyRegex = illegalKeyRegex;
    }

    public List<String> getIllegalValueRegex() {
        return illegalValueRegex;
    }

    public void setIllegalValueRegex(List<String> illegalValueRegex) {
        this.illegalValueRegex = illegalValueRegex;
    }

    public boolean isLegalKey(String key) {
        if (Objects.nonNull(illegalKeyRegex)) {
            for (String regex: illegalKeyRegex) {
                if (key.matches(regex)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLegalValue(String value) {
        if (Objects.nonNull(illegalValueRegex)) {
            for (String regex: illegalValueRegex) {
                if (value.matches(regex)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<String, AutoReplyItem> getGlobal() {
        return global;
    }

    public void setUsers(Map<String, Map<String, AutoReplyItem>> users) {
        this.users = users;
    }

    public Map<String, Map<String, AutoReplyItem>> getGroup() {
        return group;
    }

    public void setGlobal(Map<String, AutoReplyItem> global) {
        this.global = global;
    }

    public Map<String, Map<String, AutoReplyItem>> getUsers() {
        return users;
    }

    public void setGroup(Map<String, Map<String, AutoReplyItem>> group) {
        this.group = group;
    }

    public void addGlobal(String key, String answer) {
        if (Objects.isNull(global)) {
            global = new HashMap<>();
        }
        if (!global.containsKey(key)) {
            global.put(key, new AutoReplyItem(answer));
        }
        else {
            global.get(key).answers.add(answer);
        }
    }

    public void addUsers(String who, String key, String answer) {
        if (Objects.isNull(users)) {
            users = new HashMap<>();
        }
        if (!users.containsKey(who)) {
            users.put(who, new HashMap<>());
        }
        Map<String, AutoReplyItem> userMap = users.get(who);
        if (!userMap.containsKey(key)) {
            userMap.put(key, new AutoReplyItem(answer));
        }
        else {
            userMap.get(key).answers.add(answer);
        }
    }

    @Nullable
    public AutoReplyItem getUserValue(String who, String key) {
        if (Objects.nonNull(users) && users.containsKey(who)) {
            Map<String, AutoReplyItem> autoReplyItemMap = users.get(who);
            for (String string: autoReplyItemMap.keySet()) {
                AutoReplyItem item = autoReplyItemMap.get(string);
                if (string.equals(key) || (Objects.nonNull(item.alias) && item.alias.contains(key))) {
                    return item;
                }
            }
            return users.get(who).get(key);
        }
        return null;
    }

    @Nullable
    public AutoReplyItem getGlobalValue(String key) {
        if (Objects.nonNull(global)) {
            for (String string: global.keySet()) {
                AutoReplyItem item = global.get(string);
                if (string.equals(key) || (Objects.nonNull(item.alias) && item.alias.contains(key))) {
                    return item;
                }
            }
        }
        return null;
    }

    @Nullable
    public Map<String, AutoReplyItem> getUserMap(String who) {
        if (Objects.nonNull(users)) {
            return users.get(who);
        }
        return null;
    }

    public boolean hasUserMap(String who) {
        return getUserMap(who) != null;
    }

    public void removeGlobal(String key) {
        if (Objects.nonNull(global)) {
            global.remove(key);
        }
    }

    public void removeUserKey(String who, String key) {
        if (Objects.nonNull(users) && users.containsKey(who)) {
            Map<String, AutoReplyItem> userMap = users.get(who);
            userMap.remove(key);
            if (userMap.isEmpty()) {
                users.remove(userMap);
            }
        }
    }
}
