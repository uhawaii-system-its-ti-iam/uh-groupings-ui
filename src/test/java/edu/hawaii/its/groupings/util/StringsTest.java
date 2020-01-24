package edu.hawaii.its.groupings.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

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
            assertThat(Strings.truncate(s, -1), is(s));
        } catch (Exception ex) {
            assertTrue(ex instanceof IndexOutOfBoundsException);
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
