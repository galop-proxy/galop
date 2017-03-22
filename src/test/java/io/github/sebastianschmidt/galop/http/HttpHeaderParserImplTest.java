package io.github.sebastianschmidt.galop.http;

import io.github.sebastianschmidt.galop.http.HttpHeaderParser.Result;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static io.github.sebastianschmidt.galop.http.HttpConstants.HTTP_VERSION;
import static io.github.sebastianschmidt.galop.http.HttpConstants.NEW_LINE;
import static io.github.sebastianschmidt.galop.http.HttpConstants.SPACE;
import static io.github.sebastianschmidt.galop.http.HttpTestUtils.createGetRequest;
import static io.github.sebastianschmidt.galop.http.HttpTestUtils.createResponse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpHeaderParserImpl}.
 */
public class HttpHeaderParserImplTest {

    private static final int MAX_HTTP_HEADER_SIZE = 255;

    private HttpHeaderParserImpl parser;

    private String httpGetRequest;
    private long httpGetRequestLength;
    private InputStream httpGetRequestInputStream;

    private long httpResponseLength;
    private InputStream httpResponseInputStream;

    @Before
    public void setUp() {

        parser = new HttpHeaderParserImpl();

        httpGetRequest = createGetRequest();
        httpGetRequestLength = httpGetRequest.length();
        httpGetRequestInputStream = createInputStream(httpGetRequest);

        final String httpResponseContent = "<h1>Hello, world!</h1>";
        final String httpResponse = createResponse(httpResponseContent);
        httpResponseLength = httpResponse.getBytes().length;
        httpResponseInputStream = createInputStream(httpResponse);

    }

    private InputStream createInputStream(final String message) {
        return spy(IOUtils.toInputStream(message, Charset.defaultCharset()));
    }

    // Valid HTTP Requests and Responses:

    @Test
    public void parse_withoutContentLength_returnsLengthOfHeader() throws IOException {
        final Result result = parser.parse(httpGetRequestInputStream, MAX_HTTP_HEADER_SIZE);
        assertResult(false, httpGetRequestLength, httpGetRequestLength, result);
    }

    @Test
    public void parse_withContentLength_returnsLengthOfHeaderAndContent() throws IOException {
        final Result result = parser.parse(httpResponseInputStream, MAX_HTTP_HEADER_SIZE);
        assertResult(httpResponseLength, result);
    }

    @Test
    public void parse_marksAndResetsReadActions() throws IOException {

        parser.parse(httpGetRequestInputStream, MAX_HTTP_HEADER_SIZE);

        verify(httpGetRequestInputStream).mark(anyInt());
        verify(httpGetRequestInputStream).reset();

        assertEquals(httpGetRequest, IOUtils.toString(httpGetRequestInputStream, Charset.defaultCharset()));

    }

    // Invalid HTTP Requests and Responses:

    @Test(expected = InvalidHttpHeaderException.class)
    public void parse_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "a");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parse_withContentLengthLessThanZero_throwsInvalidHttpHeaderException() throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "-1");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    // Callback:

    @Test
    public void parse_withCallback_callsCallbackAsSoonAsTheFirstByteWasRead() throws IOException {

        final Runnable callback = mock(Runnable.class);

        parser.parse(httpGetRequestInputStream, MAX_HTTP_HEADER_SIZE, callback);

        final InOrder inOrder = inOrder(httpGetRequestInputStream, callback);
        inOrder.verify(httpGetRequestInputStream).read();
        inOrder.verify(callback).run();
        inOrder.verify(httpGetRequestInputStream, atLeastOnce()).read();

    }

    // Transfer-Encodings:

    @Test
    public void parse_withTransferEncodingIdentity_returnsCalculatedTotalLength() throws IOException {

        final String responseContent = "<h1>Identity Transfer-Encoding</h1>";
        final String response = createResponse(responseContent, responseContent.getBytes().length + "", "identity");
        final InputStream responseInputStream = createInputStream(response);

        final Result result = parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);
        assertResult((long) response.getBytes().length, result);

    }

    @Test
    public void parse_withTransferEncodingChunked_returnsHeaderLengthAndNoTotalLength() throws IOException {

        final String responseContent = "6" + NEW_LINE + "Hello " + NEW_LINE + "5" + NEW_LINE + "World" + NEW_LINE
                + "0" + NEW_LINE + NEW_LINE;
        final String response = createResponse(responseContent, responseContent.getBytes().length + "", "chunked");
        final long expectedHeaderLength = response.getBytes().length - responseContent.getBytes().length;
        final InputStream responseInputStream = createInputStream(response);

        final Result result = parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);
        assertResult(true, expectedHeaderLength, null, result);

    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void parse_withUnknownTransferEncoding_throwsUnsupportedTransferEncodingException() throws IOException {
        final String response = createResponse("<h1>Unknown Transfer-Encoding</h1>", null, "unknown");
        final InputStream responseInputStream = createInputStream(response);
        parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    // Case-insensitive header field names:

    @Test
    public void parse_always_ignoresUpperAndLowerCasesInHeaderFieldNames() throws IOException {

        final String response = HTTP_VERSION + SPACE + "200 OK" + NEW_LINE
                + "TRANSFER-ENCODING:" + SPACE + "chunked" + NEW_LINE + NEW_LINE;
        final InputStream responseInputStream = createInputStream(response);

        final Result result = parser.parse(responseInputStream, MAX_HTTP_HEADER_SIZE);

        assertTrue(result.isChunkedTransferEncoding());
        assertEquals(response.getBytes().length, result.getHeaderLength());
        assertNull(result.getTotalLength());

    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void parse_withoutInputStream_throwsNullPointerException() throws IOException {
        parser.parse(null, MAX_HTTP_HEADER_SIZE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_withInputStreamThatDoesNotSupportMark_throwsIllegalArgumentException() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.markSupported()).thenReturn(false);
        parser.parse(inputStream, MAX_HTTP_HEADER_SIZE);
    }

    // Helper method:

    private void assertResult(final boolean chunkedTransferEncoding, final long headerLength, final Long totalLength,
                              final Result result) {
        assertEquals(chunkedTransferEncoding, result.isChunkedTransferEncoding());
        assertEquals(headerLength, result.getHeaderLength());
        assertEquals(totalLength, result.getTotalLength());
    }

    private void assertResult(final Long totalLength, final Result result) {
        assertFalse(result.isChunkedTransferEncoding());
        assertEquals(totalLength, result.getTotalLength());
    }

}
