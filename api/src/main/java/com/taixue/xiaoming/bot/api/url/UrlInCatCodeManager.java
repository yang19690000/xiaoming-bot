package com.taixue.xiaoming.bot.api.url;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public interface UrlInCatCodeManager extends FileSavedData, UrlManager, CatCodeManager {
    static Pattern URL_PATTERN = Pattern.compile("\\[CAT:image,id=(?<id>.+),url=(?<url>.+)\\]");

    @Override
    String requireRecordedCatCode(String catCode);

    @Nullable
    String requireRecordedMessage(String message);

    @Override
    List<String> listCatCodes(String string);
}
