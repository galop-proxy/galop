package io.github.galop_proxy.api.commons;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotEmpty;
import static io.github.galop_proxy.api.commons.Preconditions.checkNotNegative;
import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests the class {@link Preconditions}.
 */
public class PreconditionsTest {

    // checkNotNull:

    @Test
    public void checkNotNull_withObject_returnsObject() {
        final Object obj = new Object();
        assertSame(obj, checkNotNull(obj, "obj"));
    }

    @Test
    public void checkNotNull_withoutObject_throwsNullPointerException() {
        try {
            checkNotNull(null, "myParameterName");
            fail("NullPointerException expected.");
        } catch (final NullPointerException ex) {
            assertEquals("myParameterName must not be null.", ex.getMessage());
        }
    }

    // checkNotNegative (int):

    @Test
    public void checkNotNegative_withZero_returnsZero() {
        assertEquals(0, checkNotNegative(0, "value"));
    }

    @Test
    public void checkNotNegative_withPositiveValue_returnsValue() {
        assertEquals(1, checkNotNegative(1, "value"));
    }

    @Test
    public void checkNotNegative_withNegativeValue_throwsIllegalArgumentException() {
        try {
            checkNotNegative(-1, "value");
            fail("IllegalArgumentException expected.");
        } catch (final IllegalArgumentException ex) {
            assertEquals("value must not be negative.", ex.getMessage());
        }
    }

    // checkNotNegative (long):

    @Test
    public void checkNotNegative_withZeroLong_returnsZero() {
        assertEquals(0L, checkNotNegative(0L, "value"));
    }

    @Test
    public void checkNotNegative_withPositiveLongValue_returnsValue() {
        assertEquals(1L, checkNotNegative(1L, "value"));
    }

    @Test
    public void checkNotNegative_withNegativeLongValue_throwsIllegalArgumentException() {
        try {
            checkNotNegative(-1L, "value");
            fail("IllegalArgumentException expected.");
        } catch (final IllegalArgumentException ex) {
            assertEquals("value must not be negative.", ex.getMessage());
        }
    }


    // checkNotEmpty:

    @Test
    public void checkNotEmpty_withNotEmptyString_returnsString() {
        assertEquals("hello world", checkNotEmpty("hello world", "string"));
    }

    @Test
    public void checkNotEmpty_withEmptyString_throwsIllegalArgumentException() {
        try {
            checkNotEmpty("", "string");
            fail("IllegalArgumentException expected.");
        } catch (final IllegalArgumentException ex) {
            assertEquals("string must not be empty.", ex.getMessage());
        }
    }

    @Test
    public void checkNotEmpty_withoutString_throwsNullPointerException() {
        try {
            checkNotEmpty(null, "string");
            fail("NullPointerException expected.");
        } catch (final NullPointerException ex) {
            assertEquals("string must not be null.", ex.getMessage());
        }
    }

    // No instances:

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
