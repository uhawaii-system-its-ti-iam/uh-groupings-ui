package edu.hawaii.its.groupings.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import edu.hawaii.its.groupings.type.Feedback;

public class JsonUtilTest {

    @Test
    public void basics() {
        Feedback fb0 = new Feedback("message");
        String fbJson = JsonUtil.asJson(fb0);

        Feedback fb1 = JsonUtil.asObject(fbJson, Feedback.class);

        assertEquals(fb0.getName(), fb1.getName());
        assertEquals(fb0.getEmail(), fb1.getEmail());
        assertEquals(fb0.getType(), fb1.getType());
        assertEquals(fb0.getMessage(), fb1.getMessage());
        assertEquals(fb0.getExceptionMessage(), fb1.getExceptionMessage());
    }

    @Test
    public void problems() {
        String json = JsonUtil.asJson(null);
        assertEquals(json, "null");

        json = JsonUtil.asJson("{}");
        assertEquals(json, "\"{}\"");

        json = JsonUtil.asJson("mistake");
        assertEquals(json, "\"mistake\"");
    }

    @Test
    public void constructorIsPrivate() throws Exception {
        Constructor<JsonUtil> constructor = JsonUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}
