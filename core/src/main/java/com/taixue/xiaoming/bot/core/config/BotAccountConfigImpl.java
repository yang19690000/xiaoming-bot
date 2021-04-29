package com.taixue.xiaoming.bot.core.config;

import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.config.BotAccountConfig;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

import java.util.ArrayList;
import java.util.List;

public class BotAccountConfigImpl extends JsonFileSavedData implements BotAccountConfig {
    private List<BotAccount> accounts = new ArrayList<>();

    @Override
    public List<BotAccount> getAccounts() {
        return accounts;
    }
}
