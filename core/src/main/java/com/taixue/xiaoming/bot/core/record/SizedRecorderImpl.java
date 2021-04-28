package com.taixue.xiaoming.bot.core.record;

import com.taixue.xiaoming.bot.api.record.SizedRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 某种东西的记录器，可以用来记录
 * @author Chuanwise
 */
public class SizedRecorderImpl<DataType> implements SizedRecorder<DataType> {
    private Queue<DataType> data = new LinkedList<>();

    @Override
    public void add(final DataType value, final int size) {
        if (data.size() >= size) {
            data.remove();
        }
        data.add(value);
    }

    @Override
    @Nullable
    public DataType latest() {
        return data.peek();
    }

    @Override
    @Nullable
    public DataType earlyest() {
        return data.poll();
    }

    @Override
    @NotNull
    public DataType[] list() {
        return ((DataType[]) data.toArray());
    }

    public Queue<DataType> getData() {
        return data;
    }

    public void setData(Queue<DataType> data) {
        this.data = data;
    }
}
