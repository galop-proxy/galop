package io.github.galop_proxy.galop.http;

import com.google.common.base.Strings;
import io.github.galop_proxy.api.http.*;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.github.galop_proxy.galop.http.HttpConstants.HEADER_CHARSET;
import static io.github.galop_proxy.galop.http.HttpConstants.NEW_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link MessageParserImpl}.
 */
public class MessageParserImplTest {

    private static final int MAX_HTTP_HEADER_SIZE = 8192;

    // Request example:

    private static final String REQUEST_HOST_EXAMPLE = "localhost";
    private static final String REQUEST_USER_AGENT_EXAMPLE = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:54.0) Gecko/20100101 Firefox/54.0";
    private static final String REQUEST_ACCEPT_EXAMPLE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String REQUEST_ACCEPT_LANGUAGE_EXAMPLE = "en-US,en;q=0.5";
    private static final String REQUEST_X_FORWARDED_FOR_1_EXAMPLE = "1.1.1.1";
    private static final String REQUEST_X_FORWARDED_FOR_2_EXAMPLE = "2.2.2.2";

    private static final String REQUEST_EXAMPLE =
              "GET /example HTTP/1.1" + NEW_LINE
            + "Host: " + REQUEST_HOST_EXAMPLE + NEW_LINE
            + "User-Agent: " + REQUEST_USER_AGENT_EXAMPLE + NEW_LINE
            + "Accept: " + REQUEST_ACCEPT_EXAMPLE + NEW_LINE
            + "Accept-Language: " + REQUEST_ACCEPT_LANGUAGE_EXAMPLE + NEW_LINE
            + "X-Forwarded-For: " + REQUEST_X_FORWARDED_FOR_1_EXAMPLE + NEW_LINE
            + "X-Forwarded-For: " + REQUEST_X_FORWARDED_FOR_2_EXAMPLE + NEW_LINE
            + "Cookie:" + NEW_LINE // Empty value
            + NEW_LINE;

    // Response example:

    private static final String RESPONSE_DATE_EXAMPLE = "Fri, 30 Jun 2017 09:23:16 GMT";
    private static final String RESPONSE_CONTENT_TYPE_EXAMPLE = "text/html; charset=utf-8";
    private static final String RESPONSE_CONTENT_EXAMPLE = "Hello world!";
    private static final String RESPONSE_CONTENT_LENGTH_EXAMPLE = RESPONSE_CONTENT_EXAMPLE.getBytes(HEADER_CHARSET).length + "";

    private static final String RESPONSE_EXAMPLE =
              "HTTP/1.1 200 OK" + NEW_LINE
            + "Date: " + RESPONSE_DATE_EXAMPLE + NEW_LINE
            + "Content-Type: " + RESPONSE_CONTENT_TYPE_EXAMPLE + NEW_LINE
            + "Content-Length: " + RESPONSE_CONTENT_LENGTH_EXAMPLE + NEW_LINE
            + NEW_LINE
            + RESPONSE_CONTENT_EXAMPLE;

    // Set up:

    private MessageParser instance;

    private Request request;
    private InputStream responseInputStream;
    private Response response;

    @Before
    public void setUp() throws IOException {

        final HttpHeaderRequestConfiguration requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        final HttpHeaderResponseConfiguration responseConfiguration = mock(HttpHeaderResponseConfiguration.class);
        when(requestConfiguration.getMaxSize()).thenReturn(MAX_HTTP_HEADER_SIZE);
        when(responseConfiguration.getMaxSize()).thenReturn(MAX_HTTP_HEADER_SIZE);

        // TODO Mockito
        final StartLineParser startLineParser = new StartLineParserImpl();
        final HeaderParser headerParser = new HeaderParserImpl();

        // TODO Constructor tests
        instance = new MessageParserImpl(requestConfiguration, responseConfiguration, startLineParser, headerParser);

        request = instance.parseRequest(toInputStream(REQUEST_EXAMPLE), null);
        responseInputStream = toInputStream(RESPONSE_EXAMPLE);
        response = instance.parseResponse(responseInputStream, null);

    }

    // parseRequest:

    @Test
    public void parseRequest_withValidHeader_returnsParsedVersion() throws IOException {
        assertEquals(new Version(1, 1), request.getVersion());
    }

    @Test
    public void parseRequest_withValidHeader_returnsParsedMethod() throws IOException {
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void parseRequest_withValidHeader_returnsParsedRequestTarget() throws IOException {
        assertEquals("/example", request.getRequestTarget());
    }

    @Test
    public void parseRequest_withValidHeader_returnsParsedHeaderFields() throws IOException {
        assertEquals(6, request.getHeaderFields().size());
        assertHeaderField(request, HeaderFields.Request.HOST, REQUEST_HOST_EXAMPLE);
        assertHeaderField(request, HeaderFields.Request.USER_AGENT, REQUEST_USER_AGENT_EXAMPLE);
        assertHeaderField(request, HeaderFields.Request.ACCEPT, REQUEST_ACCEPT_EXAMPLE);
        assertHeaderField(request, HeaderFields.Request.ACCEPT_LANGUAGE, REQUEST_ACCEPT_LANGUAGE_EXAMPLE);
        assertHeaderField(request, HeaderFields.Request.X_FORWARDED_FOR,
                REQUEST_X_FORWARDED_FOR_1_EXAMPLE, REQUEST_X_FORWARDED_FOR_2_EXAMPLE);
        assertHeaderField(request, HeaderFields.Request.COOKIE, "");
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void parseRequest_withCallback_callsCallbackAsSoonAsTheFirstByteWasRead() throws IOException {

        final InputStream inputStream = spy(toInputStream(REQUEST_EXAMPLE));
        final Runnable callback = mock(Runnable.class);

        instance.parseRequest(inputStream, callback);

        final InOrder inOrder = inOrder(inputStream, callback);
        inOrder.verify(inputStream).read();
        inOrder.verify(callback).run();
        inOrder.verify(inputStream, atLeastOnce()).read();

    }

    @Test(expected = ByteLimitExceededException.class)
    public void parseRequest_withTooLongHeader_throwsByteLimitExceededException() throws IOException {
        final String request =
                "GET /example HTTP/1.1" + NEW_LINE +
                        "Very long: " + Strings.repeat("a", MAX_HTTP_HEADER_SIZE) + NEW_LINE +
                        NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withWhitespaceBetweenHeaderFieldNameAndColon_throwsInvalidHttpHeaderException()
            throws IOException {
        final String request =
                "GET /example HTTP/1.1" + NEW_LINE +
                "Host :invalid" + NEW_LINE +
                 NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withIncompleteRequestLine_throwsInvalidHttpHeaderException() throws IOException {
        final String request = "GET /example" + NEW_LINE + NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String request = "GET /example ABC/1.1" + NEW_LINE + NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String request = "GET /example HTTP/A.1" + NEW_LINE + NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String request = "GET /example HTTP/1.B" + NEW_LINE + NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withHeaderFieldWithoutColon_throwsInvalidHttpHeaderException() throws IOException {
        final String request =
                "GET /example HTTP/1.1" + NEW_LINE +
                "WithoutColon" + NEW_LINE +
                 NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequest_withEmptyHeaderFieldName_throwsInvalidHttpHeaderException() throws IOException {
        final String request =
                "GET /example HTTP/1.1" + NEW_LINE +
                ": EmptyFieldName" + NEW_LINE +
                NEW_LINE;
        instance.parseRequest(toInputStream(request), null);
    }

    // parseResponse:

    @Test
    public void parseResponse_withValidHeader_returnsParsedVersion() {
        assertEquals(new Version(1, 1), response.getVersion());
    }

    @Test
    public void parseResponse_withValidHeader_returnsParsedStatusCode() {
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void parseResponse_withValidHeader_returnsParsedReasonPhrase() {
        assertEquals("OK", response.getReasonPhrase());
    }

    @Test
    public void parseResponse_withWhitespaceBetweenHeaderFieldNameAndColon_removesWhiteSpace()
            throws IOException {
        final String response =
                "HTTP/1.1 200 OK" + NEW_LINE +
                "Server :LoremIpsum" + NEW_LINE +
                NEW_LINE;
        final Response parsed = instance.parseResponse(toInputStream(response), null);
        assertHeaderField(parsed, HeaderFields.Response.SERVER, "LoremIpsum");
    }

    @Test
    public void parseResponse_withoutReasonPhrase_returnsEmptyReasonPhrase() throws IOException {
        final String response = "HTTP/1.1 200" + NEW_LINE + NEW_LINE;
        final Response parsed = instance.parseResponse(toInputStream(response), null);
        assertEquals("", parsed.getReasonPhrase());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void parseResponse_withCallback_callsCallbackAsSoonAsTheFirstByteWasRead() throws IOException {

        final InputStream inputStream = spy(toInputStream(RESPONSE_EXAMPLE));
        final Runnable callback = mock(Runnable.class);

        instance.parseResponse(inputStream, callback);

        final InOrder inOrder = inOrder(inputStream, callback);
        inOrder.verify(inputStream).read();
        inOrder.verify(callback).run();
        inOrder.verify(inputStream, atLeastOnce()).read();

    }

    @Test
    public void parseResponse_afterParsingHeader_doesNotReadMessageBody() throws IOException {
        assertEquals(RESPONSE_CONTENT_EXAMPLE, IOUtils.toString(responseInputStream, HEADER_CHARSET));
    }

    @Test(expected = ByteLimitExceededException.class)
    public void parseResponse_withTooLongHeader_throwsByteLimitExceededException() throws IOException {
        final String response =
                "HTTP/1.1 200 OK" + NEW_LINE +
                "Very long: " + Strings.repeat("a", MAX_HTTP_HEADER_SIZE) + NEW_LINE +
                 NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withIncompleteStatusLine_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "HTTP/1.1" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withInvalidVersionPrefix_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "ABC/1.1 200 OK" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withInvalidMajorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "HTTP/A.1 200 OK" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withInvalidMinorVersionDigit_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "HTTP/1.B 200 OK" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withTooLongStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "HTTP/1.1 2000 OK" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withInvalidStatusCode_throwsInvalidHttpHeaderException() throws IOException {
        final String response = "HTTP/1.1 ABC OK" + NEW_LINE + NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withHeaderFieldWithoutColon_throwsInvalidHttpHeaderException() throws IOException {
        final String response =
                "HTTP/1.1 200 OK" + NEW_LINE +
                "WithoutColon" + NEW_LINE +
                 NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponse_withEmptyHeaderFieldName_throwsInvalidHttpHeaderException() throws IOException {
        final String response =
                "HTTP/1.1 200 OK" + NEW_LINE +
                ": EmptyFieldName" + NEW_LINE +
                NEW_LINE;
        instance.parseResponse(toInputStream(response), null);
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void parseRequest_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.parseRequest(null, mock(Runnable.class));
    }

    @Test(expected = NullPointerException.class)
    public void parseResponse_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.parseResponse(null, mock(Runnable.class));
    }

    // Helper methods:

    private InputStream toInputStream(final String input) {
        return new ByteArrayInputStream(input.getBytes(HEADER_CHARSET));
    }

    private void assertHeaderField(final Message message, final String name, final String... expectedValue) {

        assertTrue(message.getHeaderFields().containsKey(name));

        assertEquals(expectedValue.length, message.getHeaderFields().get(name).size());

        for (int index = 0; index < expectedValue.length; index++) {
            assertEquals(expectedValue[index], message.getHeaderFields().get(name).get(index));
        }

    }

}
