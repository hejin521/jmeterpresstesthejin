package com.gotokeep.jmeter.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 *
 * Created by zhaoqian on 19/2/14.
 */
public class JsonSerializer implements Serializer {

    public static final JsonSerializer INSTANCE = new JsonSerializer();

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // SerializationFeature for changing how JSON is written

        // to enable standard indentation ("pretty-printing"):
        // OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // to allow serialization of "empty" POJOs (no fields to serialize)
        // (without this setting, an exception is thrown in those cases)
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // DeserializationFeature for changing how JSON is read as POJOs:

        // to prevent exception when encountering unknown property:
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // to allow coercion of JSON empty String ("") to null Object value:
        MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public <T> T deserialize(byte[] jsonBytes, Class<T> t) throws IOException {
        return MAPPER.readValue(jsonBytes, t);
    }

    public <T> T deserialize(byte[] jsonBytes, TypeReference<T> t) throws IOException {
        return MAPPER.readValue(jsonBytes, t);
    }

    public <T> T deserialize(String jsonString, Class<T> t) throws IOException {
        return MAPPER.readValue(jsonString, t);
    }

    public <T> T deserialize(String jsonString, TypeReference<T> t) throws IOException {
        return MAPPER.readValue(jsonString, t);
    }

    public String serialize(Object t) throws JsonProcessingException {
        if (t == null) {
            return StringUtils.EMPTY;
        }
        return MAPPER.writeValueAsString(t);
    }

    public String serializeWithPretty(Object t) throws JsonProcessingException {
        if (t == null) {
            return StringUtils.EMPTY;
        }
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(t);
    }
}
