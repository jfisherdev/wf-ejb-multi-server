package com.jfisherdev.wfejbmultiserver.commons;

/**
 * @author Josh Fisher
 */
public final class EjbStringUtils {

    public static String required(String s, String message) {
        if (!isPopulated(s)) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }

    public static boolean isPopulated(String s) {
        return !trimToEmpty(s).isEmpty();
    }

    public static String trimToEmpty(String s) {
        return s != null ? s.trim() : "";
    }

    private EjbStringUtils() {
    }
}
