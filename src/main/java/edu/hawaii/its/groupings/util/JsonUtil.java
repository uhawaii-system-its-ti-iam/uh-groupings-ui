package edu.hawaii.its.groupings.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import edu.hawaii.its.groupings.type.Announcement;

public class JsonUtil {

    private static final Log logger = LogFactory.getLog(JsonUtil.class);

    // Private constructor to prevent instantiation.
    private JsonUtil() {
        // Empty.
    }

    public static String asJson(final Object obj) {
        String result = null;
        try {
            result = new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Error: " + e);
            // Maybe we should throw something?
        }
        return result;
    }

    public static <T> T asObject(final String json, Class<T> type) {
        T result = null;
        try {
            result = new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            logger.error("Error: " + e);
            // Maybe we should throw something?
        }
        return result;
    }

    public static <T> List<Announcement> asList(final String json, Class<T> type) {
        List<Announcement> result = null;
        try {
            ObjectMapper om = new ObjectMapper();
            result = new ObjectMapper().readValue(json, listOf(type));
        } catch (Exception e) {
            logger.error("Error: " + e);
        }
        return result != null ? result : Collections.emptyList();
    }

    private static JavaType listOf(Class clazz) {
        return TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
    }

    public static void printJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(obj);
            System.err.println(json);
        } catch (Exception e) {
            logger.error("Error: " + e);
        }
    }

    public static void prettyPrint(Object object) {
        try {
            String json = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
            System.out.println(json);
        } catch (Exception e) {
            logger.error("Error: " + e);
        }
    }
}
