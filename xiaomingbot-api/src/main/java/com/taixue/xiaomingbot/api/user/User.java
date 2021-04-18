package com.taixue.xiaomingbot.api.user;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long qq;
    private String id;
    private long verifyTime;
    private String alias;
    private List<UserEvent> events;

    public long getVerifyTime() {
        return verifyTime;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setVerifyTime(long verifyTime) {
        this.verifyTime = verifyTime;
    }

    public static User user(long qq) {
        User user = new User();
        user.qq = qq;
        user.events = new ArrayList<>();
        return user;
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserEvent> getEvents() {
        return events;
    }

    public void setEvents(List<UserEvent> events) {
        this.events = events;
    }

    public void addEvent(UserEvent event) {
        events.add(event);
    }
}
