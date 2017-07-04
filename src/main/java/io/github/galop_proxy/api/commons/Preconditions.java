package io.github.galop_proxy.api.commons;

public final class Preconditions {

    public static <T> T checkNotNull(final T object, final String parameterName) throws NullPointerException {

        if (object != null) {
            return object;
        } else {
            throw new NullPointerException(parameterName + " must not be null.");
        }

    }

    public static int checkNotNegative(final int value, final String parameterName) throws IllegalArgumentException {
        if (value > -1) {
            return value;
        } else {
            throw new IllegalArgumentException(parameterName + " must not be negative.");
        }
    }

    public static long checkNotNegative(final long value, final String parameterName) throws IllegalArgumentException {
        if (value > -1) {
            return value;
        } else {
            throw new IllegalArgumentException(parameterName + " must not be negative.");
        }
    }

    public static String checkNotEmpty(final String s, final String parameterName)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(s, parameterName);

        if (!s.isEmpty()) {
            return s;
        } else {
            throw new IllegalArgumentException(parameterName + " must not be empty.");
        }

    }

    private Preconditions() {
        throw new AssertionError("No instances");
    }

}
