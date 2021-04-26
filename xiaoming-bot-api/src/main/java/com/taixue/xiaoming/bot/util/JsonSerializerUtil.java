package com.taixue.xiaoming.bot.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonSerializerUtil() {
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public <T> T readValue(final InputStream inputStream,
                           final Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }

    public void writeValue(final OutputStream outputStream,
                           final Object object)
            throws IOException {
        objectMapper.writeValue(outputStream, object);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
