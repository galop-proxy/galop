package io.github.galop_proxy.galop.commons;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNegative;
import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;
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

    // checkNotNegative:

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

    // No instances:

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
