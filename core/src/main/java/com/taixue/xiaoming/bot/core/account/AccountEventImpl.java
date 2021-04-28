package com.taixue.xiaoming.bot.core.account;


import com.taixue.xiaoming.bot.api.account.AccountEvent;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;

/**
 * @author Chuanwise
 */
public class AccountEventImpl implements AccountEvent {
    private long group;
    private long time;
    private String content;

    public static AccountEvent groupEvent(final long group,
                                          final String content) {
        AccountEvent event = new AccountEventImpl();
        event.setGroup(group);
        event.setTime(System.currentTimeMillis());
        event.setContent(content);
        return event;
    }

    public static AccountEvent groupEvent(final GroupXiaomingUser user,
                                          final String content) {
        return groupEvent(user.getGroup(), content);
    }

    public static AccountEvent privateEvent(final String content) {
        return groupEvent(-1, content);
    }

    @Override
    public long getGroup() {
        return group;
    }

    @Override
    public void setGroup(final long group) {
        this.group = group;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(final long time) {
        this.time = time;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }
}