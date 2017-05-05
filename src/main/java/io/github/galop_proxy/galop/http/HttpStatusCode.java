package io.github.galop_proxy.galop.http;

public enum HttpStatusCode {

    // Success:
    OK(200, "OK"),

    // Client errors:
    BAD_REQUEST(400, "Bad Request"),
    REQUEST_TIMEOUT(408, "Request Time-out"),
    LENGTH_REQUIRED(411, "Length Required"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),

    // Server errors:
    BAD_GATEWAY(502, "Bad Gateway"),
    GATEWAY_TIMEOUT(504, "Gateway Time-out");

    private final int code;
    private final String reason;

    HttpStatusCode(final int code, final String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

}
