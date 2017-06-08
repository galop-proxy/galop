package io.github.galop_proxy.galop.commons;

public final class Preconditions {

    public static <T> T checkNotNull(final T object, final String parameterName) throws NullPointerException {

        if (object != null) {
            return object;
        } else {
            throw new NullPointerException(parameterName + " must not be null.");
        }

    }

    private Preconditions() {
        throw new AssertionError("No instances");
    }

}
