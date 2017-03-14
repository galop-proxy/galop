package io.github.sebastianschmidt.galop;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpHeaderParser}.
 */
public class HttpHeaderParserTest {

    private static final String NEW_LINE = "\r\n";

    private HttpHeaderParser parser;

    private String httpGetRequest;
    private int httpGetRequestLength;
    private InputStream httpGetRequestInputStream;

    private int httpResponseLength;
    private InputStream httpResponseInputStream;

    @Before
    public void setUp() {

        parser = new HttpHeaderParser(255);

        httpGetRequest = "GET /hello-world.html HTTP/1.1" + NEW_LINE
                + "Host: www.example.com" + NEW_LINE
                + NEW_LINE;
        httpGetRequestLength = getBytesLength(httpGetRequest);
        httpGetRequestInputStream = createInputStream(httpGetRequest);

        final String httpResponseContent = "<h1>Hello, world!</h1>";
        final String httpResponse = createHttpResponse(httpResponseContent, getBytesLength(httpResponseContent));
        httpResponseLength = getBytesLength(httpResponse);
        httpResponseInputStream = createInputStream(httpResponse);

    }

    private InputStream createInputStream(final String message) {
        return spy(IOUtils.toInputStream(message, Charset.defaultCharset()));
    }

    private int getBytesLength(final String messages) {
        return messages.getBytes(Charset.defaultCharset()).length;
    }

    private String createHttpResponse(final String content, final String contentLength) {
        return "HTTP/1.1 200 OK" + NEW_LINE
                + "Server: Test/1.0" + NEW_LINE
                + "Content-Length: " + contentLength + NEW_LINE
                + "Content-Type: text/html" + NEW_LINE
                + NEW_LINE
                + content;
    }

    private String createHttpResponse(final String content, final int contentLength) {
        return createHttpResponse(content, contentLength + "");
    }

    @Test
    public void construct_withHeaderSizeLimitEqual255_returnsParserWithGivenHeaderSizeLimit() {
        assertEquals(255, parser.getHeaderSizeLimit());
    }

    @Test
    public void construct_withHeaderSizeLimitGreaterThan255_returnsParserWithGivenHeaderSizeLimit() {
        final HttpHeaderParser parser = new HttpHeaderParser(256);
        assertEquals(256, parser.getHeaderSizeLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_withHeaderSizeLimitLessThan255_throwsIllegalArgumentException() {
        new HttpHeaderParser(254);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_withNegativeHeaderSizeLimit_throwsIllegalArgumentException() {
        new HttpHeaderParser(-1);
    }

    @Test(expected = NullPointerException.class)
    public void calculateRequestLength_withoutInputStream_throwsNullPointerException() throws IOException {
        parser.calculateRequestLength(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRequestLength_withInputStreamThatDoesNotSupportMark_throwsIllegalArgumentException() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.markSupported()).thenReturn(false);
        parser.calculateRequestLength(inputStream);
    }

    @Test
    public void calculateRequestLength_withoutContentLength_returnsLengthOfHeader() throws IOException {
        assertEquals(httpGetRequestLength, parser.calculateRequestLength(httpGetRequestInputStream));
    }

    @Test
    public void calculateRequestLength_withContentLength_returnsLengthOfHeaderAndContent() throws IOException {
        assertEquals(httpResponseLength, parser.calculateRequestLength(httpResponseInputStream));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateRequestLength_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        final String httpResponseWithInvalidContentLength = createHttpResponse("<h1>Invalid Content-Length</h1>", "a");
        final InputStream httpResponseInputStream = createInputStream(httpResponseWithInvalidContentLength);
        parser.calculateRequestLength(httpResponseInputStream);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateRequestLength_withContentLengthLessThanZero_throwsInvalidHttpHeaderException()
            throws IOException {
        final String httpResponseWithInvalidContentLength = createHttpResponse("<h1>Invalid Content-Length</h1>", "-1");
        final InputStream httpResponseInputStream = createInputStream(httpResponseWithInvalidContentLength);
        parser.calculateRequestLength(httpResponseInputStream);
    }

    @Test
    public void calculateRequestLength_marksAndResetsReadActions() throws IOException {

        parser.calculateRequestLength(httpGetRequestInputStream);

        verify(httpGetRequestInputStream).mark(anyInt());
        verify(httpGetRequestInputStream).reset();

        assertEquals(httpGetRequest, IOUtils.toString(httpGetRequestInputStream, Charset.defaultCharset()));

    }

}
