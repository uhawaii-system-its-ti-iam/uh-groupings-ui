package edu.hawaii.its.groupings.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class StringsTest {

    @Test
    public void fill() {
        String s = Strings.fill('$', 6);
        assertThat(s, is("$$$$$$"));
    }

    @Test
    public void isNotEmpty() {
        assertTrue(Strings.isNotEmpty("t"));
        assertTrue(Strings.isNotEmpty("test"));
        assertTrue(Strings.isNotEmpty(" test "));
        assertFalse(Strings.isNotEmpty(""));
        assertFalse(Strings.isNotEmpty(" "));
        assertFalse(Strings.isNotEmpty(null));
    }

    @Test
    public void isEmpty() {
        assertFalse(Strings.isEmpty("t"));
        assertFalse(Strings.isEmpty("test"));
        assertFalse(Strings.isEmpty(" test "));
        assertTrue(Strings.isEmpty(""));
        assertTrue(Strings.isEmpty(" "));
        assertTrue(Strings.isEmpty(null));
    }

    @Test
    public void trunctate() {
        String s = "abcdefghijk";
        assertThat(Strings.truncate(s, 3), is("abc"));
        assertThat(Strings.truncate(s, 2), is("ab"));
        assertThat(Strings.truncate(s, 1), is("a"));
        assertThat(Strings.truncate(s, 0), is(""));
        assertThat(Strings.truncate(s, 11), is(s));
        assertThat(Strings.truncate(s, 12), is(s));

        assertNull(Strings.truncate(null, 0));
        assertNull(Strings.truncate(null, 1));

        // Note this result:
        try {
            Strings.truncate(s, -1);
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Strings> constructor = Strings.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
