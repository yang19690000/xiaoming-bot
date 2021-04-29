package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用于保存通用小明调用者信息的数据结构
 */
public interface Account extends FileSavedData {
    /**
     * 用户记录项（可以自行添加）
     */
    Map<String, Object> getProperties();

    /**
     * 增加或覆盖用户记录项
     */
    void putProperty(String key,
                     Object value);

    /**
     * 获得该用户屏蔽的插件
     */
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
