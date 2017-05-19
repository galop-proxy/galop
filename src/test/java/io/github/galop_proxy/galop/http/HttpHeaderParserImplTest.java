package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpHeaderParserImpl}.
 */
public class HttpHeaderParserImplTest {

    private static final int MAX_HTTP_HEADER_SIZE = 255;

    private HttpHeaderParser parser;

    private String httpGetRequest;
    private long httpGetRequestLength;
    private InputStream httpGetRequestInputStream;

    private long httpResponseLength;
    private InputStream httpResponseInputStream;

    @Before
    public void setUp() {

        final HttpHeaderRequestConfiguration requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        final HttpHeaderResponseConfiguration responseConfiguration = mock(HttpHeaderResponseConfiguration.class);
        when(requestConfiguration.getMaxSize()).thenReturn(MAX_HTTP_HEADER_SIZE);
        when(responseConfiguration.getMaxSize()).thenReturn(MAX_HTTP_HEADER_SIZE);

        parser = new HttpHeaderParserImpl(requestConfiguration, responseConfiguration);

        httpGetRequest = HttpTestUtils.createGetRequest();
        httpGetRequestLength = httpGetRequest.length();
        httpGetRequestInputStream = createInputStream(httpGetRequest);

        final String httpResponseContent = "<h1>Hello, world!</h1>";
        final String httpResponse = HttpTestUtils.createResponse(httpResponseContent);
        httpResponseLength = httpResponse.getBytes().length;
        httpResponseInputStream = createInputStream(httpResponse);

    }

    private InputStream createInputStream(final String message) {
        return spy(IOUtils.toInputStream(message, Charset.defaultCharset()));
    }

    // Valid HTTP Requests and Responses:

    @Test
    public void parse_withoutContentLength_returnsLengthOfHeader() throws IOException {
        final HttpHeaderParser.Result result = parser.parse(httpGetRequestInputStream, true);
        assertResult(false, httpGetRequestLength, httpGetRequestLength, result);
    }

    @Test
    public void parse_withContentLength_returnsLengthOfHeaderAndContent() throws IOException {
        final HttpHeaderParser.Result result = parser.parse(httpResponseInputStream, false);
        assertResult(httpResponseLength, result);
    }

    @Test
    public void parse_marksAndResetsReadActions() throws IOException {

        parser.parse(httpGetRequestInputStream, true);

        verify(httpGetRequestInputStream).mark(anyInt());
        verify(httpGetRequestInputStream).reset();

        assertEquals(httpGetRequest, IOUtils.toString(httpGetRequestInputStream, Charset.defaultCharset()));

    }

    // Invalid HTTP Requests and Responses:

    @Test(expected = InvalidHttpHeaderException.class)
    public void parse_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        final String response = HttpTestUtils.createResponse("<h1>Invalid Content-Length</h1>", "a");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, false);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parse_withContentLengthLessThanZero_throwsInvalidHttpHeaderException() throws IOException {
        final String response = HttpTestUtils.createResponse("<h1>Invalid Content-Length</h1>", "-1");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, false);
    }

    // Callback:

    @Test
    public void parse_withCallback_callsCallbackAsSoonAsTheFirstByteWasRead() throws IOException {

        final Runnable callback = mock(Runnable.class);

        parser.parse(httpGetRequestInputStream, true, callback);

        final InOrder inOrder = inOrder(httpGetRequestInputStream, callback);
        inOrder.verify(httpGetRequestInputStream).read();
        inOrder.verify(callback).run();
        inOrder.verify(httpGetRequestInputStream, atLeastOnce()).read();

    }

    // Transfer-Encodings:

    @Test
    public void parse_withTransferEncodingIdentity_returnsCalculatedTotalLength() throws IOException {

        final String responseContent = "<h1>Identity Transfer-Encoding</h1>";
        final String response = HttpTestUtils.createResponse(responseContent, responseContent.getBytes().length + "", "identity");
        final InputStream responseInputStream = createInputStream(response);

        final HttpHeaderParser.Result result = parser.parse(responseInputStream, false);
        assertResult((long) response.getBytes().length, result);

    }

    @Test
    public void parse_withTransferEncodingChunked_returnsHeaderLengthAndNoTotalLength() throws IOException {

        final String responseContent = "6" + HttpConstants.NEW_LINE + "Hello " + HttpConstants.NEW_LINE + "5" + HttpConstants.NEW_LINE + "World" + HttpConstants.NEW_LINE
                + "0" + HttpConstants.NEW_LINE + HttpConstants.NEW_LINE;
        final String response = HttpTestUtils.createResponse(responseContent, responseContent.getBytes().length + "", "chunked");
        final long expectedHeaderLength = response.getBytes().length - responseContent.getBytes().length;
        final InputStream responseInputStream = createInputStream(response);

        final HttpHeaderParser.Result result = parser.parse(responseInputStream, false);
        assertResult(true, expectedHeaderLength, null, result);

    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void parse_withUnknownTransferEncoding_throwsUnsupportedTransferEncodingException() throws IOException {
        final String response = HttpTestUtils.createResponse("<h1>Unknown Transfer-Encoding</h1>", null, "unknown");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, false);
    }

    // Case-insensitive header field names:

    @Test
    public void parse_always_ignoresUpperAndLowerCasesInHeaderFieldNames() throws IOException {

        final String response = HttpConstants.HTTP_VERSION + HttpConstants.SPACE + "200 OK" + HttpConstants.NEW_LINE
                + "TRANSFER-ENCODING:" + HttpConstants.SPACE + "chunked" + HttpConstants.NEW_LINE + HttpConstants.NEW_LINE;
        final InputStream responseInputStream = createInputStream(response);

        final HttpHeaderParser.Result result = parser.parse(responseInputStream, false);

        assertTrue(result.isChunkedTransferEncoding());
        assertEquals(response.getBytes().length, result.getHeaderLength());
        assertNull(result.getTotalLength());

    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void parse_withoutInputStream_throwsNullPointerException() throws IOException {
        parser.parse(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_withInputStreamThatDoesNotSupportMark_throwsIllegalArgumentException() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.markSupported()).thenReturn(false);
        parser.parse(inputStream, true);
    }

    // Helper method:

    private void assertResult(final boolean chunkedTransferEncoding, final long headerLength, final Long totalLength,
                              final HttpHeaderParser.Result result) {
        assertEquals(chunkedTransferEncoding, result.isChunkedTransferEncoding());
        assertEquals(headerLength, result.getHeaderLength());
        assertEquals(totalLength, result.getTotalLength());
    }

    private void assertResult(final Long totalLength, final HttpHeaderParser.Result result) {
        assertFalse(result.isChunkedTransferEncoding());
        assertEquals(totalLength, result.getTotalLength());
    }

}
