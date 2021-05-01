package com.taixue.xiaoming.bot.api.error;

import java.util.List;

public interface ErrorMessage {
    long getQq();

    long getGroup();

    long getTime();

    String getGroupAlias();

    String getQqAlias();

    List<String> getInputs();

    String getMessage();
}
