package com.taixue.xiaoming.bot.core.error;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.error.ErrorMessage;
import com.taixue.xiaoming.bot.api.error.ErrorMessageManager;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessageManagerImpl extends JsonFileSavedData implements ErrorMessageManager {
    private List<ErrorMessage> errorMessages = new ArrayList<>();

    @Override
    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public void addErrorMessage(ErrorMessage message) {
        errorMessages.add(message);
    }
}
