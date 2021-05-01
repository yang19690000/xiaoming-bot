package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import net.mamoe.mirai.event.events.UserEvent;
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

    void addEvent(AccountEvent event);

    long getQq();

    void setQq(long qq);

    String getAlias();

    void setAlias(String alias);

    List<AccountEvent> getEvents();

    void setEvents(List<AccountEvent> events);

    List<AccountEvent> getHistories();

    void setHistories(List<AccountEvent> histories);

    void addHistory(AccountEvent event);
}
