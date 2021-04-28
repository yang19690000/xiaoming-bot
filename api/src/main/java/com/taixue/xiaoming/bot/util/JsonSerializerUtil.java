package com.taixue.xiaoming.bot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Chuanwise
 */
public class JsonSerializerUtil {
    private static final JsonSerializerUtil INSTANCE = new JsonSerializerUtil();

    public static JsonSerializerUtil getInstance() {
        return INSTANCE;
    }

    private static final SerializerFeature[] FEATURES = new SerializerFeature[] {
            SerializerFeature.WriteClassName,
            SerializerFeature.IgnoreNonFieldGetter
    };

    // private ObjectMapper objectMapper = new ObjectMapper();

    private JsonSerializerUtil() {
        /*
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
         */
    }

    public <T> T readValue(final InputStream inputStream,
                           final Class<T> clazz)
            throws IOException {
        //return objectMapper.readValue(inputStream, clazz);
        return JSON.parseObject(inputStream, clazz);
    }

    public void writeValue(final OutputStream outputStream,
                           final Object object)
            throws IOException {
        JSON.writeJSONString(outputStream, object, FEATURES);
    }

    public <T> T convert(@NotNull final Object o, @NotNull Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(o, FEATURES), clazz).get(0);
    }
}
