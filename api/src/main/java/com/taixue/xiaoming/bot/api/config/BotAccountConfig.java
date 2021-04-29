package com.taixue.xiaoming.bot.api.config;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

import java.util.List;

public interface BotAccountConfig extends FileSavedData {
    List<BotAccount> getAccounts();
}
