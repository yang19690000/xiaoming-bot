package com.taixue.xiaoming.bot.api.account;


import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;

/**
 * @author Chuanwise
 */
public class AccountEvent {
    private long group;
    private long time;
    private String content;

    public static AccountEvent groupEvent(final long group,
                                          final String content) {
        AccountEvent event = new AccountEvent();
        event.group = group;
        event.time = System.currentTimeMillis();
        event.content = content;
        return event;
    }

    public static AccountEvent groupEvent(final GroupXiaomingUser user,
                                          final String content) {
        return groupEvent(user.getGroup(), content);
    }

    public static AccountEvent privateEvent(String content) {
        return groupEvent(-1, content);
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
