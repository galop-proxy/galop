package io.github.galop_proxy.galop.http;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Tests the class {@link HttpConstants}.
 */
public class HttpConstantsTest {

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<HttpConstants> constructor = HttpConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
