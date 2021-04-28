package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;

public interface AccountEvent {

    long getGroup();

    void setGroup(final long group);

    long getTime();

    void setTime(final long time);

    String getContent();

    void setContent(String content);
}
