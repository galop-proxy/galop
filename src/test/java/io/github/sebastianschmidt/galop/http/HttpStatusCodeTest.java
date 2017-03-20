package io.github.sebastianschmidt.galop.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpStatusCode}.
 */
public class HttpStatusCodeTest {

    @Test
    public void ok() {
        assertStatus(200, "OK", HttpStatusCode.OK);
    }

    @Test
    public void badRequest() {
        assertStatus(400, "Bad Request", HttpStatusCode.BAD_REQUEST);
    }

    @Test
    public void requestTimeOut() {
        assertStatus(408, "Request Time-out", HttpStatusCode.REQUEST_TIMEOUT);
    }

    @Test
    public void lengthRequired() {
        assertStatus(411, "Length Required", HttpStatusCode.LENGTH_REQUIRED);
    }

    @Test
    public void requestHeaderFieldsTooLarge() {
        assertStatus(431, "Request Header Fields Too Large", HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
    }

    @Test
    public void badGateway() {
        assertStatus(502, "Bad Gateway", HttpStatusCode.BAD_GATEWAY);
    }

    @Test
    public void gatewayTimeOut() {
        assertStatus(504, "Gateway Time-out", HttpStatusCode.GATEWAY_TIMEOUT);
    }

    private void assertStatus(final int code, final String reason, final HttpStatusCode httpStatusCode) {
        assertEquals(code, httpStatusCode.getCode());
        assertEquals(reason, httpStatusCode.getReason());
    }

}
