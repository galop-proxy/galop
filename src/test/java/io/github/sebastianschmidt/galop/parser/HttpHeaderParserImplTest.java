package io.github.sebastianschmidt.galop.parser;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static io.github.sebastianschmidt.galop.parser.HttpTestUtils.createGetRequest;
import static io.github.sebastianschmidt.galop.parser.HttpTestUtils.createResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpHeaderParserImpl}.
 */
public class HttpHeaderParserImplTest {

    private static final int MAX_HTTP_HEADER_SIZE = 255;

    private HttpHeaderParserImpl parser;

    private String httpGetRequest;
    private int httpGetRequestLength;
    private InputStream httpGetRequestInputStream;

    private int httpResponseLength;
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
    public void calculateTotalLength_withoutContentLength_returnsLengthOfHeader() throws IOException {
        assertEquals(httpGetRequestLength, parser.calculateTotalLength(httpGetRequestInputStream, MAX_HTTP_HEADER_SIZE));
    }

    @Test
    public void calculateTotalLength_withContentLength_returnsLengthOfHeaderAndContent() throws IOException {
        assertEquals(httpResponseLength, parser.calculateTotalLength(httpResponseInputStream, MAX_HTTP_HEADER_SIZE));
    }

    @Test
    public void calculateTotalLength_marksAndResetsReadActions() throws IOException {

        parser.calculateTotalLength(httpGetRequestInputStream, MAX_HTTP_HEADER_SIZE);

        verify(httpGetRequestInputStream).mark(anyInt());
        verify(httpGetRequestInputStream).reset();

        assertEquals(httpGetRequest, IOUtils.toString(httpGetRequestInputStream, Charset.defaultCharset()));

    }

    // Invalid HTTP Requests and Responses:

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateTotalLength_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "a");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void calculateTotalLength_withContentLengthLessThanZero_throwsInvalidHttpHeaderException()
            throws IOException {
        final String response = createResponse("<h1>Invalid Content-Length</h1>", "-1");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    // Transfer-Encodings:

    @Test
    public void calculateTotalLength_withTransferEncodingIdentity_returnsCalculatedLength() throws IOException {
        final String responseContent = "<h1>Identity Transfer-Encoding</h1>";
        final String response = createResponse(responseContent, responseContent.getBytes().length + "", "identity");
        final InputStream responseInputStream = createInputStream(response);
        assertEquals(response.getBytes().length, parser.calculateTotalLength(responseInputStream, MAX_HTTP_HEADER_SIZE));
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void calculateTotalLength_withTransferEncodingChunked_throwsUnsupportedTransferEncodingException()
            throws IOException {
        final String response = createResponse("<h1>Chunked Transfer-Encoding</h1>", null, "chunked");
        final InputStream responseInputStream = createInputStream(response);
        parser.calculateTotalLength(responseInputStream, MAX_HTTP_HEADER_SIZE);
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void calculateTotalLength_withoutInputStream_throwsNullPointerException() throws IOException {
        parser.calculateTotalLength(null, MAX_HTTP_HEADER_SIZE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateTotalLength_withInputStreamThatDoesNotSupportMark_throwsIllegalArgumentException() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.markSupported()).thenReturn(false);
        parser.calculateTotalLength(inputStream, MAX_HTTP_HEADER_SIZE);
    }

}
