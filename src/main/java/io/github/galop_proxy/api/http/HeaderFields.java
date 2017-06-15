package io.github.galop_proxy.api.http;

@SuppressWarnings("unused")
public final class HeaderFields {

    public static final class Request {

        public static final String ACCEPT = "accept";
        public static final String ACCEPT_CHARSET = "accept-charset";
        public static final String ACCEPT_ENCODING = "accept-encoding";
        public static final String ACCEPT_LANGUAGE = "accept-language";
        public static final String ACCEPT_DATETIME = "accept-datetime";
        public static final String AUTHORIZATION = "authorization";
        public static final String CACHE_CONTROL = "cache-control";
        public static final String CONNECTION = "connection";
        public static final String COOKIE = "cookie";
        public static final String CONTENT_LENGTH = "content-length";
        public static final String CONTENT_TYPE = "content-type";
        public static final String DATE = "date";
        public static final String EXPECT = "expect";
        public static final String FORWARDED = "forwarded";
        public static final String FROM = "from";
        public static final String HOST = "host";
        public static final String IF_MATCH = "if-match";
        public static final String IF_MODIFIED_SINCE = "if-modified-since";
        public static final String IF_NONE_MATCH = "if-none-match";
        public static final String IF_RANGE = "if-range";
        public static final String IF_UNMODIFIED_SINCE = "if-unmodified-since";
        public static final String MAX_FORWARDS = "max-forwards";
        public static final String ORIGIN = "origin";
        public static final String PRAGMA = "pragma";
        public static final String PROXY_AUTHORIZATION = "proxy-authorization";
        public static final String RANGE = "range";
        @SuppressWarnings("SpellCheckingInspection")
        public static final String REFERRER = "referer";
        public static final String TE = "te";
        public static final String USER_AGENT = "user-agent";
        public static final String UPGRADE = "upgrade";
        public static final String VIA = "via";
        public static final String WARNING = "warning";

        // Common non-standard request fields:
        public static final String X_FORWARDED_FOR = "x-forwarded-for";
        public static final String X_FORWARDED_HOST = "x-forwarded-host";
        public static final String X_FORWARDED_PROTO = "x-forwarded-proto";

        private Request() {
            throw new AssertionError("No instances");
        }

    }

    public static final class Response {

        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "access-control-allow-origin";
        public static final String ACCEPT_PATCH = "accept-patch";
        public static final String AGE = "age";
        public static final String ALLOW = "allow";
        public static final String ALT_SVC = "alt-svc";
        public static final String CACHE_CONTROL = "cache-control";
        public static final String CONNECTION = "connection";
        public static final String CONTENT_DISPOSITION = "content-disposition";
        public static final String CONTENT_ENCODING = "content-encoding";
        public static final String CONTENT_LANGUAGE = "content-language";
        public static final String CONTENT_LENGTH = "content-length";
        public static final String CONTENT_LOCATION = "content-location";
        public static final String CONTENT_RANGE = "content-range";
        public static final String CONTENT_TYPE = "content-type";
        public static final String DATE = "date";
        @SuppressWarnings("SpellCheckingInspection")
        public static final String ETAG = "etag";
        public static final String EXPIRES = "expires";
        public static final String LAST_MODIFIED = "last-modified";
        public static final String LINK = "link";
        public static final String LOCATION = "location";
        public static final String P3P = "p3p";
        public static final String PRAGMA = "pragma";
        public static final String PROXY_AUTHENTICATE = "proxy-authenticate";
        public static final String PUBLIC_KEY_PINS = "public-key-pins";
        public static final String REFRESH = "refresh";
        public static final String RETRY_AFTER = "retry-after";
        public static final String SERVER = "server";
        public static final String SET_COOKIE = "set-cookie";
        public static final String STRICT_TRANSPORT_SECURITY = "strict-transport-security";
        public static final String TRAILER = "trailer";
        public static final String TRANSFER_ENCODING = "transfer-encoding";
        public static final String TK = "tk";
        public static final String UPGRADE = "upgrade";
        public static final String VARY = "vary";
        public static final String VIA = "via";
        public static final String WARNING = "warning";
        public static final String WWW_AUTHENTICATE = "www-authenticate";
        public static final String X_FRAME_OPTIONS = "x-frame-options";

        // Common non-standard response fields:
        public static final String X_POWERED_BY = "x-powered-by";
        public static final String X_UA_COMPATIBLE = "x-ua-compatible";

        private Response() {
            throw new AssertionError("No instances");
        }

    }

    private HeaderFields() {
        throw new AssertionError("No instances");
    }

}
