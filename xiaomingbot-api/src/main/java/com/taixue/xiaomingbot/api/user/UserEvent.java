package com.taixue.xiaomingbot.api.user;

import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUser;
import com.taixue.xiaomingbot.util.DateUtil;

public class UserEvent {
    private long qq;
    private long group;
    private long time;
    private String content;

    public static UserEvent groupEvent(long group, long qq, String content) {
        UserEvent event = new UserEvent();
        event.group = group;
        event.qq = qq;
        event.time = System.currentTimeMillis();
        event.content = content;
        return event;
    }

    public static UserEvent groupEvent(GroupInteractorUser user, String content) {
        return groupEvent(user.getGroup(), user.getQQ(), content);
    }

    public static UserEvent privateEvent(long qq, String content) {
        return groupEvent(-1, qq, content);
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public long getGroup() {
        return group;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
