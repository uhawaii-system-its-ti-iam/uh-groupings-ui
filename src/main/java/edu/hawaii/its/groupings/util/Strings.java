package edu.hawaii.its.groupings.util;

import java.util.Arrays;

public class Strings {

    // Private constructor; prevent instantiation.
    private Strings() {
        // Emtpy.
    }

    public static String fill(final char ch, final int size) {
        char[] fill = new char[size];
        Arrays.fill(fill, ch);

        return new String(fill);
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String truncate(String value, int length) {
        String s = value;
        if (s != null && s.length() > length) {
            s = s.substring(0, length);
        }
        return s;
    }
}
