package io.github.sebastianschmidt.galop;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static io.github.sebastianschmidt.galop.HttpTestUtils.createHttpRequest;
import static io.github.sebastianschmidt.galop.HttpTestUtils.createHttpResponse;
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

        httpGetRequest = createHttpRequest();
        httpGetRequestLength = httpGetRequest.length();
        httpGetRequestInputStream = createInputStream(httpGetRequest);

        final String httpResponseContent = "<h1>Hello, world!</h1>";
        final String httpResponse = createHttpResponse(httpResponseContent);
        httpResponseLength = httpResponse.getBytes().length;
        httpResponseInputStream = createInputStream(httpResponse);

    }

    private InputStream createInputStream(final String message) {
        return spy(IOUtils.toInputStream(message, Charset.defaultCharset()));
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
