package com.taixue.xiaomingbot.api.group;

import com.alibaba.fastjson.JSON;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.GroupMemberList;
import love.forte.simbot.api.sender.MsgSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class GroupManager {
    protected transient File file;
    protected Map<String, Group> groups;

    public static GroupManager forFile(File file) {
        GroupManager manager = null;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                manager = JSON.parseObject(fileInputStream, GroupManager.class);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Objects.isNull(manager)) {
            manager = new GroupManager();
            manager.save();
        }
        manager.file = file;
        return manager;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    public Set<Group> getGroups(String nameStartWith) {
        Set<Group> result = new HashSet<>();
        if (Objects.isNull(groups)) {
            return result;
        }
        for (String s : groups.keySet()) {
            if (s.startsWith(nameStartWith)) {
                result.add(groups.get(s));
            }
        }
        return result;
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

    public void addGroup(String name, Group group) {
        if (Objects.isNull(groups)) {
            groups = new HashMap<>();
        }
        groups.put(name, group);
        save();
    }

    public boolean hasGroup(String name) {
        return !getGroups(name).isEmpty();
    }

    public boolean isGroup(String name, long group) {
        for (Group g : getGroups(name)) {
            if (g.getCode() == group) {
                return true;
            }
        }
        return false;
    }

    public static boolean groupContains(long groupCode, long qq, MsgSender sender) {
        try {
            GroupMemberList groupMemberList = sender.GETTER.getGroupMemberList(groupCode);
            for (GroupMemberInfo info : groupMemberList) {
                if (info.getAccountCodeNumber() == qq) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }
}
