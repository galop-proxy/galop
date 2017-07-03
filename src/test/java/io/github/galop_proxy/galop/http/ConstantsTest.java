package io.github.galop_proxy.galop.http;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link Constants}.
 */
public class ConstantsTest {

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void newLineConsistsOfACarriageReturnAndALineFeed() {
        assertEquals("\r\n", Constants.NEW_LINE);
    }

}
