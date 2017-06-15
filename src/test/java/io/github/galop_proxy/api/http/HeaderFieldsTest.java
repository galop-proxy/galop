package io.github.galop_proxy.api.http;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Tests the class {@link HeaderFields}.
 */
public class HeaderFieldsTest {

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<HeaderFields> constructor = HeaderFields.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test(expected = Exception.class)
    public void constructor_Request_throwsException() throws Exception {
        final Constructor<HeaderFields.Request> constructor = HeaderFields.Request.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test(expected = Exception.class)
    public void constructor_Response_throwsException() throws Exception {
        final Constructor<HeaderFields.Response> constructor = HeaderFields.Response.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
