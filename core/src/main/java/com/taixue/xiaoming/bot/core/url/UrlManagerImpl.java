package com.taixue.xiaoming.bot.core.url;

import com.taixue.xiaoming.bot.api.url.UrlManager;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;

import java.util.HashSet;
import java.util.Set;

public abstract class UrlManagerImpl extends JsonFileSavedData implements UrlManager {
    private Set<String> urls = new HashSet<>();

    @Override
    public boolean contains(final String url) {
        return urls.contains(url);
    }

    @Override
    public String requireRecordedUrl(final String url) {
        return contains(url) ? url : null;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }
}