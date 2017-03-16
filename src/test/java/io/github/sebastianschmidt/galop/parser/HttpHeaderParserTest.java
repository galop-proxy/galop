package io.github.sebastianschmidt.galop.parser;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static io.github.sebastianschmidt.galop.HttpTestUtils.createGetRequest;
import static io.github.sebastianschmidt.galop.HttpTestUtils.createResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpHeaderParser}.
 */
public class HttpHeaderParserTest {

    private HttpHeaderParser parser;

    private String httpGetRequest;
    private int httpGetRequestLength;
    private InputStream httpGetRequestInputStream;

    private int httpResponseLength;
    private InputStream httpResponseInputStream;

    @Before
    public void setUp() {

        parser = new HttpHeaderParser(255);

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
    public void calculateTotalLength_withoutContentLength_returnsLengthOfHeader() throws IOException {
        assertEquals(httpGetRequestLength, parser.calculateTotalLength(httpGetRequestInputStream));
    }

    @Test
    public void calculateTotalLength_withContentLength_returnsLengthOfHeaderAndContent() throws IOException {
        assertEquals(httpResponseLength, parser.calculateTotalLength(httpResponseInputStream));
    }

    @Test
    public void calculateTotalLength_marksAndResetsReadActions() throws IOException {

        parser.calculateTotalLength(httpGetRequestInputStream);

        verify(httpGetRequestInputStream).mark(anyInt());
        verify(httpGetRequestInputStream).reset();

        assertEquals(httpGetRequest, IOUtils.toString(httpGetRequestInputStream, Charset.defaultCharset()));

    }

    // Invalid HTTP Requests and Responses:

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateTotalLength_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "a");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateTotalLength_withContentLengthLessThanZero_throwsInvalidHttpHeaderException()
            throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "-1");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream);
    }

    // Transfer-Encodings:

    @Test
    public void calculateTotalLength_withTransferEncodingIdentity_returnsCalculatedLength() throws IOException {
        final String responseContent = "<h1>Identity Transfer-Encoding</h1>";
        final String response = createResponse(responseContent, responseContent.getBytes().length + "", "identity");
        final InputStream responseInputStream = createInputStream(response);
        assertEquals(response.getBytes().length, parser.calculateTotalLength(responseInputStream));
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void calculateTotalLength_withTransferEncodingChunked_throwsUnsupportedTransferEncodingException()
            throws IOException {
        final String response = createResponse("<h1>Chunked Transfer-Encoding</h1>", null, "chunked");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream);
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void calculateTotalLength_withoutInputStream_throwsNullPointerException() throws IOException {
        parser.calculateTotalLength(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateTotalLength_withInputStreamThatDoesNotSupportMark_throwsIllegalArgumentException() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.markSupported()).thenReturn(false);
        parser.calculateTotalLength(inputStream);
    }

    // constructor:

    @Test
    public void constructor_withHeaderSizeLimitEqual255_returnsParserWithGivenHeaderSizeLimit() {
        assertEquals(255, parser.getHeaderSizeLimit());
    }

    @Test
    public void constructor_withHeaderSizeLimitGreaterThan255_returnsParserWithGivenHeaderSizeLimit() {
        final HttpHeaderParser parser = new HttpHeaderParser(256);
        assertEquals(256, parser.getHeaderSizeLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withHeaderSizeLimitLessThan255_throwsIllegalArgumentException() {
        new HttpHeaderParser(254);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNegativeHeaderSizeLimit_throwsIllegalArgumentException() {
        new HttpHeaderParser(-1);
    }


}
