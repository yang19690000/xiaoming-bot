package com.taixue.xiaoming.bot.api.error;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

import java.util.List;

public interface ErrorMessageManager extends FileSavedData {
    List<ErrorMessage> getErrorMessages();

    void addErrorMessage(ErrorMessage message);
}
