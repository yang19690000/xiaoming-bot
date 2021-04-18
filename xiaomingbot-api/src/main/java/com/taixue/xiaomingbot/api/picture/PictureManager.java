package com.taixue.xiaomingbot.api.picture;

import catcode.CatCodeUtil;
import com.alibaba.fastjson.JSON;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PictureManager {
    public static final String URL_MAP_FILE_NAME = "urldata.json";
    public static final Pattern PICTURE_URL_PATTERN = Pattern.compile(".*image,id=\\{.+\\}\\.(\\w+),url=(.+)");

    public transient File directory;
    public transient File urlMapFile;

    private static final Random RANDOM = new Random();

    public Map<String, String> urlMap;

    public static PictureManager forFile(File directory) {
        File urlMapFile = new File(directory, URL_MAP_FILE_NAME);
        PictureManager result = null;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(urlMapFile)) {
                result = JSON.parseObject(fileInputStream, PictureManager.class);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Objects.isNull(result)) {
            result = new PictureManager();
            result.urlMap = new HashMap<>();
        }
        if (Objects.isNull(result.urlMap)) {
            result.urlMap = new HashMap<>();
        }
        result.directory = directory;
        result.urlMapFile = urlMapFile;
        return result;
    }

    public void save() {
        try {
            if (!urlMapFile.exists() || urlMapFile.isDirectory()) {
                urlMapFile.createNewFile();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(urlMapFile)) {
                fileOutputStream.write(JSON.toJSONString(this).getBytes());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public File getImageFile(String name, String extension) {
        return new File(directory, name + "." + extension);
    }

    public boolean hasUrlIndex(String url) {
        return urlMap.containsKey(url);
    }

    @NotNull
    public File getSavedUrlImageFile(String url) {
        return new File(directory, urlMap.get(url));
    }

    public File getJPGFile(String name) {
        return getImageFile(name, "jpg");
    }

    public File getPNGFile(String name) {
        return getImageFile(name, "png");
    }

    /**
     * 在现有的 URL 表中添加新的图片
     * @param urlString     URL
     * @param fileName      图片名。为其在 picture 中的相对路径
     */
    public void putUrlImageIndex(String urlString, String fileName) {
        urlMap.put(urlString, fileName);
        save();
    }

    /**
     * 将网络图片保存在 ./picture 内
     * @param urlString 图片的
     * @return 如果出现异常，返回 null，否则返回下载到的文件名
     */
    @Nullable
    public File saveImageFile(File saveTo, String urlString) throws IOException {
        URL imageURL = new URL(urlString);
        try (InputStream inputStream = imageURL.openStream();
             FileOutputStream fileOutputStream = new FileOutputStream(saveTo)) {

            byte[] bytes = new byte[1024];
            int readLength = 0;

            while ((readLength = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, readLength);
            }
        }
        return saveTo;
    }

    /**
     * 获取或保存并获取某个图片
     * @param catCodeString
     * @return               如果 url 错误、格式错误或发生异常，返回 null
     * @throws IOException
     */
    @Nullable
    public File getOrSaveImageFile(String catCodeString) throws IOException {
        String urlString;
        String extension;

        Matcher matcher = PICTURE_URL_PATTERN.matcher(catCodeString);
        if (matcher.matches()) {
            extension = matcher.group(1);
            urlString = matcher.group(2);
        }
        else {
            return null;
        }
        if (hasUrlIndex(urlString)) {
            return getSavedUrlImageFile(urlString);
        }

        String fileName = String.valueOf(Math.abs(RANDOM.nextLong()));
        File file = getImageFile(fileName, extension);
        // 确保得到的是新的文件名
        while (file.exists()) {
            fileName = String.valueOf(Math.abs(RANDOM.nextLong()));
            file = getImageFile(fileName, extension);
        }
        putUrlImageIndex(urlString, file.getName());
        file.createNewFile();
        return saveImageFile(file, urlString);
    }

    /**
     * 保存网络图片到本地并替换其值
     * @param string
     * @return 如果保存图片出现问题为 null，否则为替换 url 后的图片
     */
    @Nullable
    public String transUrlString(String string) {
        int left = string.indexOf("[CAT:image");
        int right = Integer.MAX_VALUE;
        if (left != -1) {
            // 用于构造最终的文字链接的 StringBuilder
            StringBuilder builder = new StringBuilder(string);
            while (left < right) {
                right = builder.indexOf("]", left);
                if (left < right) {
                    String catCodeString = string.substring(left + 1, right);
                    try {
                        File file = getOrSaveImageFile(catCodeString);
                        String replaceTo = CatCodeUtil.getInstance().getStringTemplate().image(file.getAbsolutePath());
                        builder.replace(left, right + 1, replaceTo);
                        left += replaceTo.length();
                        right = left + 1;
                    }
                    catch (IOException e) {
                        return null;
                    }
                }
            }
            return builder.toString();
        }
        else {
            return string;
        }
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
