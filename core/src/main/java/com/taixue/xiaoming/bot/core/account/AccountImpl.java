package com.taixue.xiaoming.bot.core.account;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountEvent;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 用于存储的账号信息
 * @author Chuanwise
 */
public class AccountImpl extends JsonFileSavedData implements Account {
    private long qq;
    private String alias;
    private List<AccountEvent> commands = new ArrayList<>();
    private List<AccountEvent> histories = new ArrayList<>();
    private Set<String> blockPlugins = new HashSet<>();
    private Map<String, Object> properties = new HashMap<>();

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public void putProperty(final String key,
                            final Object value) {
        properties.put(key, value);
        save();
    }

    @Override
    public Set<String> getBlockPlugins() {
        return blockPlugins;
    }

    @Override
    public void addBlockPlugin(final String plugin) {
        blockPlugins.add(plugin);
    }

    @Override
    public boolean isBlockPlugin(final String plugin) {
        return blockPlugins.contains(plugin);
    }

    @Override
    @Nullable
    public <T> T getProperty(final String key,
                             final Class<T> clazz) {
        final Object object = properties.get(key);
        if (Objects.nonNull(object)) {
            if (clazz.isAssignableFrom(object.getClass())) {
                return ((T) object);
            } else {
                final T t = JsonSerializerUtil.getInstance().convert(object, clazz);
                properties.replace(key, t);
                return t;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public void addGroupMessage(final long group,
                                final String message) {
        commands.add(AccountEventImpl.groupEvent(group, message));
    }

    @Override
    public void addPrivateMessage(final String message) {
        commands.add(AccountEventImpl.privateEvent("执行指令：" + message));
    }

    @Override
    public long getQq() {
        return qq;
    }

    @Override
    public void setQq(long qq) {
        this.qq = qq;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public List<AccountEvent> getCommands() {
        return commands;
    }

    @Override
    public void setCommands(List<AccountEvent> commands) {
        this.commands = commands;
    }

    @Override
    public List<AccountEvent> getHistories() {
        return histories;
    }

    @Override
    public void setHistories(List<AccountEvent> histories) {
        this.histories = histories;
    }

    @Override
    public void addHistory(final AccountEvent event) {
        commands.add(event);
    }

    public void setBlockPlugins(Set<String> blockPlugins) {
        this.blockPlugins = blockPlugins;
    }
}