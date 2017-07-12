package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link StartLineParserImpl}.
 */
public class StartLineParserImplTest {

    private static final String REQUEST_LINE = "GET /example HTTP/1.1";
    private static final String STATUS_LINE = "HTTP/1.1 200 OK";

    private StartLineParser instance;
    private Request request;
    private Response response;

    @Before
    public void setUp() throws IOException {
        instance = new StartLineParserImpl();
        request = instance.parseRequestLine(() -> REQUEST_LINE);
        response = instance.parseStatusLine(() -> STATUS_LINE);
    }

    // parseRequestLine:

    @Test
    public void parseRequestLine_withValidHeader_returnsParsedVersion() throws IOException {
        assertEquals(new Version(1, 1), request.getVersion());
    }

    @Test
    public void parseRequestLine_withValidHeader_returnsParsedMethod() throws IOException {
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void parseRequestLine_withValidHeader_returnsParsedRequestTarget() throws IOException {
        assertEquals("/example", request.getRequestTarget());
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withIncompleteRequestLine_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example";
        instance.parseRequestLine(() -> requestLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example ABC/1.1";
        instance.parseRequestLine(() -> requestLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example HTTP/A.1";
        instance.parseRequestLine(() -> requestLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example HTTP/1.B";
        instance.parseRequestLine(() -> requestLine);
    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void parseRequestLine_withUnsupportedHttpVersion_throwsUnsupportedHttpVersionException() throws IOException {
        final String requestLine = "GET /example HTTP/1.0";
        instance.parseRequestLine(() -> requestLine);
    }

    @Test(expected = NullPointerException.class)
    public void parseRequestLine_withoutCallable_throwsNullPointerException() throws IOException {
        instance.parseRequestLine(null);
    }

    // parseStatusLine:

    @Test
    public void parseStatusLine_withValidHeader_returnsParsedVersion() {
        assertEquals(new Version(1, 1), response.getVersion());
    }

    @Test
    public void parseStatusLine_withValidHeader_returnsParsedStatusCode() {
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void parseStatusLine_withValidHeader_returnsParsedReasonPhrase() {
        assertEquals("OK", response.getReasonPhrase());
    }

    @Test
    public void parseStatusLine_withReasonPhraseContainingSpace_returnsParsedReasonPhrase() throws IOException {
        response = instance.parseStatusLine(() -> "HTTP/1.1 304 Not Modified");
        assertEquals("Not Modified", response.getReasonPhrase());
    }

    @Test
    public void parseStatusLine_withoutReasonPhrase_returnsEmptyReasonPhrase() throws IOException {
        final String statusLine = "HTTP/1.1 200";
        final Response parsed = instance.parseStatusLine(() -> statusLine);
        assertEquals("", parsed.getReasonPhrase());
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withIncompleteStatusLine_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "ABC/1.1 200 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/A.1 200 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.B 200 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withTooLongStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1 2000 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1 ABC OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void parseStatusLine_withUnsupportedHttpVersion_throwsUnsupportedHttpVersionException() throws IOException {
        final String statusLine = "HTTP/2.0 200 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = UnsupportedStatusCodeException.class)
    public void parseStatusLine_withStatusCode426UpgradeRequired_throwsUnsupportedStatusCodeException() throws IOException {
        final String statusLine = "HTTP/1.1 426 OK";
        instance.parseStatusLine(() -> statusLine);
    }

    @Test(expected = NullPointerException.class)
    public void parseStatusLine_withoutCallable_throwsNullPointerException() throws IOException {
        instance.parseStatusLine(null);
    }

}
