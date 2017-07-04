package io.github.galop_proxy.galop.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link StatusCode}.
 */
public class StatusCodeTest {

    @Test
    public void ok() {
        assertStatus(200, "OK", StatusCode.OK);
    }

    @Test
    public void badRequest() {
        assertStatus(400, "Bad Request", StatusCode.BAD_REQUEST);
    }

    @Test
    public void requestTimeOut() {
        assertStatus(408, "Request Time-out", StatusCode.REQUEST_TIMEOUT);
    }

    @Test
    public void lengthRequired() {
        assertStatus(411, "Length Required", StatusCode.LENGTH_REQUIRED);
    }

    @Test
    public void requestHeaderFieldsTooLarge() {
        assertStatus(431, "Request Header Fields Too Large", StatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
    }

    @Test
    public void badGateway() {
        assertStatus(502, "Bad Gateway", StatusCode.BAD_GATEWAY);
    }

    @Test
    public void gatewayTimeOut() {
        assertStatus(504, "Gateway Time-out", StatusCode.GATEWAY_TIMEOUT);
    }

    private void assertStatus(final int code, final String reason, final StatusCode statusCode) {
        assertEquals(code, statusCode.getCode());
        assertEquals(reason, statusCode.getReason());
    }

}
