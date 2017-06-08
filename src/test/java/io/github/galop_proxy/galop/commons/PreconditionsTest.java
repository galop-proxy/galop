package io.github.galop_proxy.galop.commons;

import org.junit.Test;

import java.lang.reflect.Constructor;

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

    // No instances:

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
