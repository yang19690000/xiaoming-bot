package com.taixue.xiaoming.bot.core.emoji;

import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EmojiManagerImpl extends JsonFileSavedData implements EmojiManager {
    private transient static final Random RANDOM = new Random();
    private Map<String, Set<String>> values = new HashMap<>();

    @Override
    public Map<String, Set<String>> getValues() {
        return values;
    }

    @Override
    @Nullable
    public Set<String> getSet(final String key) {
        return values.get(key);
    }

    @Override
    public String get(final String key) {
        final Set<String> strings = getSet(key);
        if (Objects.isNull(strings)) {
            return key;
        } else {
            return strings.toArray(new String[0])[RANDOM.nextInt(strings.size())];
        }
    }
}
