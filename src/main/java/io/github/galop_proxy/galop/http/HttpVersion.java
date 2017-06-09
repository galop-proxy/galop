package io.github.galop_proxy.galop.http;

import java.util.Objects;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNegative;
import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class HttpVersion implements Comparable<HttpVersion> {

    private final int major;
    private final int minor;

    HttpVersion(final int major, final int minor) {
        this.major = checkNotNegative(major, "major");
        this.minor = checkNotNegative(minor, "minor");
    }

    int getMajor() {
        return major;
    }

    int getMinor() {
        return minor;
    }

    boolean isGreaterThan(final HttpVersion other) {

        checkNotNull(other, "other");

        if (major == other.major) {
            return minor > other.minor;
        }

        return major > other.major;

    }

    boolean isLowerThan(final HttpVersion other) {

        checkNotNull(other, "other");

        if (major == other.major) {
            return minor < other.minor;
        }

        return major < other.major;

    }

    @Override
    public int compareTo(final HttpVersion other) {

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

        final HttpVersion that = (HttpVersion) other;

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
