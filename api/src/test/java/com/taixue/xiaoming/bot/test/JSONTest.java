package com.taixue.xiaoming.bot.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;

public class JSONTest {
    private Object object = new ArrayIndexOutOfBoundsException();

    public JSONTest() {}

    public Object getObject() {
        System.out.println("get");
        return object;
    }

    public void setObject(Object object) {
        System.out.println("set");
        this.object = object;
    }

    public static void main(String[] args) {
        final JSONTest jsonTest = new JSONTest();
        ObjectMapper mapper = new ObjectMapper();

        try {

            if (false) {
                System.out.println(mapper.readValue("{\"object\":{\"cause\":null,\"stackTrace\":[{\"methodName\":\"<init>\",\"fileName\":\"JSONTest.java\",\"lineNumber\":11,\"className\":\"com.taixue.xiaoming.bot.test.JSONTest\",\"nativeMethod\":false},{\"methodName\":\"main\",\"fileName\":\"JSONTest.java\",\"lineNumber\":26,\"className\":\"com.taixue.xiaoming.bot.test.JSONTest\",\"nativeMethod\":false}],\"localizedMessage\":null,\"message\":null,\"suppressed\":[]}}",
                        JSONTest.class).object);
            } else {
                System.out.println(mapper.writeValueAsString(jsonTest));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
