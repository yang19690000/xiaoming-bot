package com.taixue.xiaoming.bot.api.url;

import com.taixue.xiaoming.bot.api.data.FileSavedData;

public interface UrlManager {
    boolean contains(String url);

    String requireRecordedUrl(String url);
}
