package com.taixue.xiaomingbot.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taixue.xiaomingbot.api.user.User;
import com.taixue.xiaomingbot.api.user.UserEvent;
import com.taixue.xiaomingbot.api.user.UserManager;
import com.taixue.xiaomingbot.util.JSONFileData;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserFileTranslator {
    public static final Pattern PATTERN = Pattern.compile("在群\\s*(\\d+)\\s*中\\s*(.+)");
    public static void main(String[] args) {
        final File from = new File("users.elder.json");
        final File to = new File("users.json");

        if (!from.exists()) {
            System.err.println("can not found the from file!");
            return;
        }
        try {
            UserManager manager = UserManager.forFileOrNew(to, UserManager.class, () -> {
                UserManager m = new UserManager();
                m.setUsers(new HashMap<>());
                return m;
            });
            JSONObject jsonObject;
            try (FileInputStream fileInputStream = new FileInputStream(from)) {
                byte[] bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                jsonObject = JSON.parseObject(new String(bytes));
            }
            final List users = (List) jsonObject.get("users");
            for (Object user : users) {
                Map map = ((Map) user);
                User u = new User();
                u.setEvents(new ArrayList<>());
                u.setAlias(((String) map.get("alias")));
                u.setId(((String) map.get("id")));
                final Object o = map.get("qq");
                if (o instanceof Integer) {
                    u.setQq(((Integer) o));
                }
                else {
                    u.setQq(((Long) o));
                }
                final List messages = (List) map.get("messages");
                if (Objects.nonNull(messages)) {
                    for (Object message : messages) {
                        UserEvent event = new UserEvent();
                        final Map m = (Map) message;
                        event.setTime(((long)m.get("time")));
                        final String content = (String) m.get("content");
                        final Matcher matcher = PATTERN.matcher(content);
                        if (matcher.matches()) {
                            u.addEvent(UserEvent.groupEvent(Long.parseLong(matcher.group(1)), u.getQq(), matcher.group(2)));
                        }
                        else {
                            System.err.println(user);
                            return;
                        }
                    }
                }
                else {
                    u.setEvents(new ArrayList<>());
                }
                manager.addUser(u);
            }
            manager.save();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
