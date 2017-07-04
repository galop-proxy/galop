package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.HeaderFields;
import io.github.galop_proxy.api.http.Message;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link HeaderWriterImpl}.
 */
public class HeaderWriterImplTest {

    private HeaderWriter instance;

    private Message message;

    private ByteArrayOutputStream outputStream;
    private Writer writer;

    @Before
    public void setUp() {

        instance = new HeaderWriterImpl();

        message = mock(Message.class);
        when(message.getHeaderFields()).thenReturn(new HashMap<>());

        outputStream = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(outputStream);

    }

    @Test
    public void writeHeader_withoutMultipleHeaderFieldsWithSameName_writesHeaderFieldsToOutputStreamWriter()
            throws IOException {

        message.getHeaderFields().put(HeaderFields.Request.HOST, Collections.singletonList("localhost"));
        message.getHeaderFields().put(HeaderFields.Request.USER_AGENT, Collections.singletonList("Lorem ipsum"));

        instance.writeHeader(message, writer);

        assertHeader(
                  "host: localhost" + NEW_LINE
                + "user-agent: Lorem ipsum" + NEW_LINE
                + NEW_LINE);

    }

    @Test
    public void writeHeader_withMultipleHeaderFieldsWithSameName_writesHeaderFieldsToOutputStreamWriter()
            throws IOException {

        message.getHeaderFields().put(HeaderFields.Response.SET_COOKIE,
                Arrays.asList("sessionId=123456789; HttpOnly; Path=/", "lorem=ipsum; HttpOnly; Path=/"));

        instance.writeHeader(message, writer);

        assertHeader(
                  "set-cookie: sessionId=123456789; HttpOnly; Path=/" + NEW_LINE
                + "set-cookie: lorem=ipsum; HttpOnly; Path=/" + NEW_LINE
                + NEW_LINE);

    }

    @Test
    public void writeHeader_withoutHeaderFields_writesEmptyHeaderToOutputStreamWriter() throws IOException {
        instance.writeHeader(message, writer);
        assertHeader(NEW_LINE);
    }

    @Test(expected = NullPointerException.class)
    public void writeHeader_withoutMessage_throwsNullPointerException() throws IOException {
        instance.writeHeader(null, writer);
    }

    @Test(expected = NullPointerException.class)
    public void writeHeader_withoutWriter_throwsNullPointerException() throws IOException {
        instance.writeHeader(message, null);
    }

    // Helper method:

    private void assertHeader(final String expected) {
        try {
            final String actual = outputStream.toString(Constants.HEADER_CHARSET.name());
            assertEquals(expected, actual);
        } catch (final UnsupportedEncodingException ex) {
            fail("UnsupportedEncodingException: " + ex.getMessage());
        }
    }

}
