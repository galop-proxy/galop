package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the class {@link ResponseImpl}.
 */
public class ResponseImplTest {

    private Version version;
    private int statusCode;
    private String reasonPhrase;
    private Response instance;

    @Before
    public void setUp() {
        version = new Version(1, 1);
        statusCode = 200;
        reasonPhrase = "OK";
        instance = new ResponseImpl(version, statusCode, reasonPhrase);
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutVersion_throwsNullPointerException() {
        new ResponseImpl(null, statusCode, reasonPhrase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNegativeStatusCode_throwsIllegalArgumentException() {
        new ResponseImpl(version, -1, reasonPhrase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withTooLargeStatusCode_throwsIllegalArgumentException() {
        new ResponseImpl(version, 1000, reasonPhrase);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutReasonPhrase_throwsNullPointerException() {
        new ResponseImpl(version, statusCode, null);
    }

    // getStatusCode:

    @Test
    public void getStatusCode_afterObjectCreation_returnsGivenStatusCode() {
        assertEquals(statusCode, instance.getStatusCode());
    }

    // setStatusCode:

    @Test
    public void setStatusCode_withValidStatusCode_overwritesInitialStatusCode() {
        final int otherStatusCode = 404;
        instance.setStatusCode(otherStatusCode);
        assertNotEquals(statusCode, otherStatusCode);
        assertEquals(otherStatusCode, instance.getStatusCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStatusCode_withNegativeStatusCode_throwsIllegalArgumentException() {
        instance.setStatusCode(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStatusCode_withTooLargeStatusCode_throwsIllegalArgumentException() {
        instance.setStatusCode(1000);
    }

    // getReasonPhrase:

    @Test
    public void getReasonPhrase_afterObjectCreation_returnsGivenReasonPhrase() {
        assertEquals(reasonPhrase, instance.getReasonPhrase());
    }

    // setReasonPhrase:

    @Test
    public void setReasonPhrase_withString_overwritesInitialReasonPhrase() {
        final String otherReasonPhrase = "Not Found";
        instance.setReasonPhrase(otherReasonPhrase);
        assertNotEquals(reasonPhrase, otherReasonPhrase);
        assertEquals(otherReasonPhrase, instance.getReasonPhrase());
    }

    @Test
    public void setReasonPhrase_withEmptyString_overwritesInitialReasonPhrase() {
        instance.setReasonPhrase("");
        assertEquals("", instance.getReasonPhrase());
    }

}
