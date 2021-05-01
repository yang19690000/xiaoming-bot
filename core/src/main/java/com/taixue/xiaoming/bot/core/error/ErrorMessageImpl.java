package com.taixue.xiaoming.bot.core.error;

import com.taixue.xiaoming.bot.api.error.ErrorMessage;

import java.util.List;

public class ErrorMessageImpl implements ErrorMessage {
    private long qq = 0;
    private long group = 0;
    private String groupAlias;
    private String qqAlias;
    private long time = System.currentTimeMillis();
    private String message;
    private List<String> inputs;

    /**
     * 为了反序列化器设计的默认无参构造方法
     */
    public ErrorMessageImpl() {}

    public ErrorMessageImpl(long qq, String qqAlias, long group, String groupAlias, String message, List<String> inputs) {
        this.qq = qq;
        this.group = group;
        this.groupAlias = groupAlias;
        this.qqAlias = qqAlias;
        this.message = message;
        this.inputs = inputs;
    }

    public ErrorMessageImpl(long qq, String qqAlias, String message, List<String> inputs) {
        this.qq = qq;
        this.group = group;
        this.groupAlias = groupAlias;
        this.qqAlias = qqAlias;
        this.message = message;
        this.inputs = inputs;
    }

    @Override
    public long getQq() {
        return qq;
    }

    @Override
    public long getGroup() {
        return group;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getGroupAlias() {
        return groupAlias;
    }

    @Override
    public String getQqAlias() {
        return qqAlias;
    }

    @Override
    public List<String> getInputs() {
        return inputs;
    }

    public String getMessage() {
        return message;
    }
}
