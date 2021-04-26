package com.taixue.xiaoming.bot.api.picture;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PictureManager extends JsonFileSavedData {
    private Set<Picture> urls = new HashSet<>();
    public transient static final Pattern URL_PATTERN = Pattern.compile("\\[CAT:image,id=(?<id>.+),url=(?<url>.+)\\]");

    public boolean contains(final String url) {
        return Objects.nonNull(get(url));
    }

    @Nullable
    public Picture get(final String url) {
        for (Picture picture : urls) {
            if (picture.getUrl().equals(url)) {
                return picture;
            }
        }
        return null;
    }

    public String requireRecordedUrl(final String url) {
        final Picture picture = get(url);
        if (Objects.nonNull(picture)) {
            picture.updateLastRequestTime();
            save();
            return url;
        } else {
            final Picture e = new Picture(url);
            urls.add(e);
            save();
            return url;
        }
    }

    public String requireRecordedCatCode(final String catCode) {
        Matcher matcher = URL_PATTERN.matcher(catCode);
        if (matcher.matches()) {
            return requireRecordedUrl(matcher.group("url"));
        } else {
            throw new IllegalArgumentException("syntax error: cat code string: " + catCode);
        }
    }

    /**
     * 保存网络图片到本地并替换其值
     * @param message
     * @return 如果保存图片出现问题为 null，否则为替换 url 后的图片
     */
    @Nullable
    public String requireRecordedMessage(String message) {
        for (String pictureCatCode : getPictureCatCodes(message)) {
            requireRecordedCatCode(pictureCatCode);
        }
        return message;
    }

    /**
     * 获取一段文字中的所有图片
     * @param string
     * @return
     */
    public List<String> getPictureCatCodes(String string) {
        List<String> result = new ArrayList<>();
        int left = string.indexOf("[CAT:image");
        int right = Integer.MAX_VALUE;

        while (left != -1) {
            right = string.indexOf("]", left);
            if (left < right) {
                String catCodeString = string.substring(left, right + 1);
                result.add(catCodeString);
            }
            left = string.indexOf("[CAT:image", right);
        }
        return result;
    }
}