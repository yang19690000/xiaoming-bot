package com.taixue.xiaoming.bot.api.emoji;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EmojiManager extends JsonFileSavedData {
    private transient static final Random RANDOM = new Random();
    private Map<String, Set<String>> values = new HashMap<>();

    public Map<String, Set<String>> getValues() {
        return values;
    }

    public void setValues(Map<String, Set<String>> values) {
        this.values = values;
    }

    @Nullable
    public Set<String> getSet(String key) {
        return values.get(key);
    }

    public String get(String key) {
        final Set<String> strings = getSet(key);
        if (Objects.isNull(strings)) {
            return key;
        }
        else {
            return strings.toArray(new String[0])[RANDOM.nextInt(strings.size())];
        }
    }
}
