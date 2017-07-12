package io.github.galop_proxy.galop.http;

public enum StatusCode {

    // Success:
    OK(200, "OK"),

    // Client errors:
    BAD_REQUEST(400, "Bad Request"),
    REQUEST_TIMEOUT(408, "Request Time-out"),
    LENGTH_REQUIRED(411, "Length Required"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),

    // Server errors:
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Time-out"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int code;
    private final String reason;

    StatusCode(final int code, final String reason) {
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
