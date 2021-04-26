package com.taixue.xiaoming.bot.api.picture;

import com.taixue.xiaoming.bot.api.base.HostObject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Picture extends HostObject {
    private String url;
    private long lastRequestTime;

    public Picture() {}

    public Picture(@NotNull final String url) {
        setUrl(url);
        updateLastRequestTime();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void updateLastRequestTime() {
        lastRequestTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return url.equals(picture.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
