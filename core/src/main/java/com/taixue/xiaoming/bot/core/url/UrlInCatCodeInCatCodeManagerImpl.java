package com.taixue.xiaoming.bot.core.url;

import com.taixue.xiaoming.bot.api.url.UrlInCatCodeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;

public class UrlInCatCodeInCatCodeManagerImpl extends UrlManagerImpl implements UrlInCatCodeManager {
    @Override
    public String requireRecordedCatCode(final String catCode) {
        requireRecordedUrl(requireUrl(catCode));
        return catCode;
    }

    @Override
    @NotNull
    public String requireUrl(final String catCode) {
        Matcher matcher = URL_PATTERN.matcher(catCode);
        if (matcher.matches()) {
            return matcher.group("url");
        } else {
            throw new IllegalArgumentException("syntax error: cat code string: " + catCode);
        }
    }

    /**
     * 保存网络图片到本地并替换其值
     * @param message
     * @return 如果保存图片出现问题为 null，否则为替换 url 后的图片
     */
    @Override@Nullable
    public String requireRecordedMessage(String message) {
        for (String pictureCatCode : listCatCodes(message)) {
            requireRecordedCatCode(pictureCatCode);
        }
        return message;
    }

    /**
     * 获取一段文字中的所有图片
     * @param string
     * @return
     */
    @Override
    public List<String> listCatCodes(String string) {
        List<String> result = new ArrayList<>();
        int left = string.indexOf("[CAT:image");
        int right;

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