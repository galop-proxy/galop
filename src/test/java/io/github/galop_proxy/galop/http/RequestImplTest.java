package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Version;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the class {@link RequestImpl}.
 */
public class RequestImplTest {

    private Version version;
    private String method;
    private String requestTarget;
    private Request instance;

    @Before
    public void setUp() {
        version = new Version(1, 1);
        method = "GET";
        requestTarget = "/hello/world";
        instance = new RequestImpl(version, method, requestTarget);
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutVersion_throwsNullPointerException() {
        new RequestImpl(null, method, requestTarget);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMethod_throwsNullPointerException() {
        new RequestImpl(version, null, requestTarget);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withEmptyMethod_throwsIllegalArgumentException() {
        new RequestImpl(version, "", requestTarget);
    }


    @Test(expected = NullPointerException.class)
    public void constructor_withoutRequestTarget_throwsNullPointerException() {
        new RequestImpl(version, method, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withEmptyRequestTarget_throwsIllegalArgumentException() {
        new RequestImpl(version, method, "");
    }

    // getMethod:

    @Test
    public void getMethod_afterObjectCreation_returnsGivenMethod() {
        assertEquals(method, instance.getMethod());
    }

    // setMethod:

    @Test
    public void setMethod_withNotEmptyString_overwritesInitialMethod() {
        final String otherMethod = "POST";
        instance.setMethod(otherMethod);
        assertNotEquals(method, otherMethod);
        assertEquals(otherMethod, instance.getMethod());
    }

    @Test(expected = NullPointerException.class)
    public void setMethod_withoutString_throwsNullPointerException() {
        instance.setMethod(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMethod_withEmptyString_throwsIllegalArgumentException() {
        instance.setMethod("");
    }

    // getRequestTarget:

    @Test
    public void getRequestTarget_afterObjectCreation_returnsGivenRequestTarget() {
        assertEquals(requestTarget, instance.getRequestTarget());
    }

    // setRequestTarget:

    @Test
    public void setRequestTarget_witNotEmptyString_overwritesInitialRequestTarget() {
        final String otherRequestTarget = "/lorem/ipsum";
        instance.setRequestTarget(otherRequestTarget);
        assertNotEquals(requestTarget, otherRequestTarget);
        assertEquals(otherRequestTarget, instance.getRequestTarget());
    }

    @Test(expected = NullPointerException.class)
    public void setRequestTarget_withoutString_throwsNullPointerException() {
        instance.setRequestTarget(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRequestTarget_withEmptyString_throwsIllegalArgumentException() {
        instance.setRequestTarget("");
    }

}
