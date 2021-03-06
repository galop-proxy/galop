package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        final HttpHeaderRequestConfiguration requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        final HttpHeaderResponseConfiguration responseConfiguration = mock(HttpHeaderResponseConfiguration.class);

        when(requestConfiguration.getRequestLineSizeLimit()).thenReturn(64);
        when(responseConfiguration.getStatusLineSizeLimit()).thenReturn(64);

        instance = new StartLineParserImpl(requestConfiguration, responseConfiguration);

        request = instance.parseRequestLine(create(REQUEST_LINE));
        response = instance.parseStatusLine(create(STATUS_LINE));

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
        instance.parseRequestLine(create(requestLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example ABC/1.1";
        instance.parseRequestLine(create(requestLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example HTTP/A.1";
        instance.parseRequestLine(create(requestLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestLine_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String requestLine = "GET /example HTTP/1.B";
        instance.parseRequestLine(create(requestLine));
    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void parseRequestLine_withUnsupportedHttpVersion_throwsUnsupportedHttpVersionException() throws IOException {
        final String requestLine = "GET /example HTTP/1.0";
        instance.parseRequestLine(create(requestLine));
    }

    @Test(expected = LineTooLargeException.class)
    public void parseRequestLine_withTooLargeRequestLine_throwsLineTooLargeException() throws IOException {
        final String longSuffix = StringUtils.repeat("a", 64);
        final String requestLine = "GET /example/?" + longSuffix + " HTTP/1.1";
        instance.parseRequestLine(create(requestLine));
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
        final String statusLine = "HTTP/1.1 304 Not Modified";
        response = instance.parseStatusLine(create(statusLine));
        assertEquals("Not Modified", response.getReasonPhrase());
    }

    @Test
    public void parseStatusLine_withoutReasonPhrase_returnsEmptyReasonPhrase() throws IOException {
        final String statusLine = "HTTP/1.1 200";
        final Response parsed = instance.parseStatusLine(create(statusLine));
        assertEquals("", parsed.getReasonPhrase());
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withIncompleteStatusLine_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "ABC/1.1 200 OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/A.1 200 OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.B 200 OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withTooLongStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1 2000 OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseStatusLine_withInvalidStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String statusLine = "HTTP/1.1 ABC OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void parseStatusLine_withUnsupportedHttpVersion_throwsUnsupportedHttpVersionException() throws IOException {
        final String statusLine = "HTTP/2.0 200 OK";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = UnsupportedStatusCodeException.class)
    public void parseStatusLine_withStatusCode426UpgradeRequired_throwsUnsupportedStatusCodeException() throws IOException {
        final String statusLine = "HTTP/1.1 426 Upgrade Required";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = UnsupportedStatusCodeException.class)
    public void parseStatusLine_withStatusCode101SwitchingProtocols_throwsUnsupportedStatusCodeException() throws IOException {
        final String statusLine = "HTTP/1.1 101 Switching Protocols";
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = LineTooLargeException.class)
    public void parseStatusLine_withTooLargeStatusLine_throwsLineTooLargeException() throws IOException {
        final String longSuffix = StringUtils.repeat("a", 64);
        final String statusLine = "HTTP/1.1 200 " + longSuffix;
        instance.parseStatusLine(create(statusLine));
    }

    @Test(expected = NullPointerException.class)
    public void parseStatusLine_withoutCallable_throwsNullPointerException() throws IOException {
        instance.parseStatusLine(null);
    }
    
    // Helper method:
    
    private LineReader create(final String startLine) {
        final String withCrLb = startLine + "\r\n";
        return new LineReader(new ByteArrayInputStream(withCrLb.getBytes()));
    }

}
