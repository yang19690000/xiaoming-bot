package com.taixue.xiaoming.bot.api.limit;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

public interface UserCallLimitManager extends FileSavedData {
    UserCallLimiter getGroupCallLimiter();

    UserCallLimiter getPrivateCallLimiter();
}
