package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Account extends FileSavedData {
    Map<String, Object> getProperties();

    void putProperty(String key,
                     Object value);

    Set<String> getBlockPlugins();

    void addBlockPlugin(String plugin);

    boolean isBlockPlugin(String plugin);

    @Nullable
    <T> T getProperty(String key,
                      Class<T> clazz);

    void setProperties(Map<String, Object> properties);

    void addGroupMessage(long group,
                         String message);

    void addPrivateMessage(String message);

    long getQq();

    void setQq(long qq);

    String getAlias();

    void setAlias(String alias);

    List<AccountEvent> getCommands();

    void setCommands(List<AccountEvent> commands);

    List<AccountEvent> getHistories();

    void setHistories(List<AccountEvent> histories);

    void addHistory(AccountEvent event);
}
