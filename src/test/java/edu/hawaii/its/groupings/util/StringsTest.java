package edu.hawaii.its.groupings.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        assertThat(Strings.truncate(s, 3), equalTo("abc"));
        assertThat(Strings.truncate(s, 2), equalTo("ab"));
        assertThat(Strings.truncate(s, 1), equalTo("a"));
        assertThat(Strings.truncate(s, 0), equalTo(""));
        assertThat(Strings.truncate(s, 11), equalTo(s));
        assertThat(Strings.truncate(s, 12), equalTo(s));

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
