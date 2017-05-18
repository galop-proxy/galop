package io.github.galop_proxy.galop.commons;

import java.util.Objects;

public final class PortNumber {

    public static boolean isValidPortNumber(final int value) {
        return 0 <= value && value <= 65535;
    }

    public static PortNumber parsePortNumber(final String s) {

        final int value;

        try {
            value = Integer.parseInt(s);
        } catch (final NumberFormatException ex) {
            return null;
        }

        if (isValidPortNumber(value)) {
            return new PortNumber(value);
        } else {
            return null;
        }

    }

    private final int value;

    public PortNumber(final int value) {

        if (!isValidPortNumber(value)) {
            throw new IllegalArgumentException("Port number out of range: " + value);
        }

        this.value = value;

    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final PortNumber that = (PortNumber) other;
        return value == that.value;

    }

}
