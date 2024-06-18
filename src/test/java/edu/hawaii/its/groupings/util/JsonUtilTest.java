package edu.hawaii.its.groupings.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
    @Test
    public void testAsList() {

        Feedback fb1 = new Feedback();
        fb1.setName("testiwa");
        fb1.setEmail("testiwa@hawaii.edu");
        fb1.setType("general");
        fb1.setMessage("Hello World");
        fb1.setExceptionMessage(null);

        Feedback fb2 = new Feedback();
        fb2.setName("testiwb");
        fb2.setEmail("testiwb@hawaii.edu");
        fb2.setType("support");
        fb2.setMessage("Goodbye World");
        fb2.setExceptionMessage(null);

        String fb1Json = JsonUtil.asJson(fb1);
        String fb2Json = JsonUtil.asJson(fb2);

        String jsonArray = "[" + fb1Json + "," + fb2Json + "]";

        List<Feedback> feedbackList = JsonUtil.asList(jsonArray, Feedback.class);

        assertNotNull(feedbackList);
        assertEquals(2, feedbackList.size());

        Feedback retrievedFb1 = feedbackList.get(0);
        assertEquals(fb1.getName(), retrievedFb1.getName());
        assertEquals(fb1.getEmail(), retrievedFb1.getEmail());
        assertEquals(fb1.getType(), retrievedFb1.getType());
        assertEquals(fb1.getMessage(), retrievedFb1.getMessage());

        Feedback retrievedFb2 = feedbackList.get(1);
        assertEquals(fb2.getName(), retrievedFb2.getName());
        assertEquals(fb2.getEmail(), retrievedFb2.getEmail());
        assertEquals(fb2.getType(), retrievedFb2.getType());
        assertEquals(fb2.getMessage(), retrievedFb2.getMessage());
    }
}
