package com.taixue.xiaoming.bot.api.account;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 用于存储的账号信息
 * @author Chuanwise
 */
public class Account extends JsonFileSavedData {
    private long qq;
    private String alias;
    private List<AccountEvent> commands = new ArrayList<>();
    private List<AccountEvent> histories = new ArrayList<>();
    private Set<String> blockPlugins = new HashSet<>();
    private Map<String, Object> properties = new HashMap<>();

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void putProperty(final String key,
                            final Object value) {
        properties.put(key, value);
        save();
    }

    public Set<String> getBlockPlugins() {
        return blockPlugins;
    }

    public void addBlockPlugin(final String plugin) {
        blockPlugins.add(plugin);
    }

    public boolean isBlockPlugin(final String plugin) {
        return blockPlugins.contains(plugin);
    }

    @Nullable
    public <T> T getProperty(final String key,
                           final Class<T> clazz) {
        final Object object = properties.get(key);
        if (Objects.nonNull(object)) {
            if (object.getClass().equals(clazz)) {
                return ((T) object);
            } else {
                final T t = JsonSerializerUtil.getInstance().getObjectMapper().convertValue(object, clazz);
                properties.replace(key, t);
                return t;
            }
        } else {
            return null;
        }
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addGroupCommandEvent(final long group,
                                     final String command) {
        commands.add(AccountEvent.groupEvent(group, command));
    }

    public void addPrivateCommandEvent(final String command) {
        commands.add(AccountEvent.privateEvent("执行指令：" + command));
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<AccountEvent> getCommands() {
        return commands;
    }

    public void setCommands(List<AccountEvent> commands) {
        this.commands = commands;
    }

    public List<AccountEvent> getHistories() {
        return histories;
    }

    public void setHistories(List<AccountEvent> histories) {
        this.histories = histories;
    }

    public void addHistory(final AccountEvent event) {
        commands.add(event);
    }
}
