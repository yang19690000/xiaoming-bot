package com.taixue.xiaoming.bot.api.emoji;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface EmojiManager extends FileSavedData {
    Map<String, Set<String>> getValues();

    @Nullable
    Set<String> getSet(String key);

    String get(String key);
}
