package io.github.galop_proxy.api.http;

import java.util.Objects;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNegative;
import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;

    public Version(final int major, final int minor) {
        this.major = checkNotNegative(major, "major");
        this.minor = checkNotNegative(minor, "minor");
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public boolean isGreaterThan(final Version other) {

        checkNotNull(other, "other");

        if (major == other.major) {
            return minor > other.minor;
        }

        return major > other.major;

    }

    public boolean isLowerThan(final Version other) {

        checkNotNull(other, "other");

        if (major == other.major) {
            return minor < other.minor;
        }

        return major < other.major;

    }

    @Override
    public int compareTo(final Version other) {

        if (isGreaterThan(other)) {
            return 1;
        } else if (isLowerThan(other)) {
            return -1;
        } else {
            return 0;
        }

    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Version that = (Version) other;

        return major == that.major &&
                minor == that.minor;

    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor);
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }

}
