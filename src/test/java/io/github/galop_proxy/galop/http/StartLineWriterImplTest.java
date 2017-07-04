package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link StartLineWriterImpl}.
 */
public class StartLineWriterImplTest {

    private StartLineWriter instance;

    private Request request;
    private Response response;

    private ByteArrayOutputStream outputStream;
    private Writer writer;


    @Before
    public void setUp() {

        instance = new StartLineWriterImpl();

        request = mock(Request.class);
        response = mock(Response.class);

        outputStream = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(outputStream);

    }

    // writeRequestLine:

    @Test
    public void writeRequestLine_withRequest_writesRequestLineToOutputStreamWriter() throws IOException {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestTarget()).thenReturn("/example");
        when(request.getVersion()).thenReturn(new Version(1, 0));

        instance.writeRequestLine(request, writer);

        assertStartLine("GET /example HTTP/1.0" + NEW_LINE);

    }

    @Test(expected = NullPointerException.class)
    public void writeRequestLine_withoutRequest_throwsNullPointerException() throws IOException {
        instance.writeRequestLine(null, writer);
    }

    @Test(expected = NullPointerException.class)
    public void writeRequestLine_withoutOutputStreamWriter_throwsNullPointerException() throws IOException {
        instance.writeRequestLine(request, null);
    }

    // writeStatusLine:

    @Test
    public void writeStatusLine_withRequest_writesStatusLineToOutputStreamWriter() throws IOException {

        when(response.getVersion()).thenReturn(new Version(1, 0));
        when(response.getStatusCode()).thenReturn(200);
        when(response.getReasonPhrase()).thenReturn("OK");

        instance.writeStatusLine(response, writer);

        assertStartLine("HTTP/1.0 200 OK" + NEW_LINE);

    }

    @Test
    public void writeStatusLine_withoutReasonPhrase_writesStatusLineWithEmptyReasonPhraseToOutputStreamWriter()
            throws IOException {

        when(response.getVersion()).thenReturn(new Version(1, 1));
        when(response.getStatusCode()).thenReturn(500);
        when(response.getReasonPhrase()).thenReturn("");

        instance.writeStatusLine(response, writer);

        assertStartLine("HTTP/1.1 500 " + NEW_LINE);

    }

    @Test(expected = NullPointerException.class)
    public void writeStatusLine_withoutResponse_throwsNullPointerException() throws IOException {
        instance.writeStatusLine(null, writer);
    }

    @Test(expected = NullPointerException.class)
    public void writeStatusLine_withoutWriter_throwsNullPointerException() throws IOException {
        instance.writeStatusLine(response, null);
    }

    // Helper method:

    private void assertStartLine(final String expected) {
        try {
            final String actual = outputStream.toString(Constants.HEADER_CHARSET.name());
            assertEquals(expected, actual);
        } catch (final UnsupportedEncodingException ex) {
            fail("UnsupportedEncodingException: " + ex.getMessage());
        }
    }

}
