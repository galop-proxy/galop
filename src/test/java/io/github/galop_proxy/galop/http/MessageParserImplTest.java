package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link MessageParserImpl}.
 */
public class MessageParserImplTest {

    private StartLineParser startLineParser;
    private HeaderParser headerParser;
    private MessageParser instance;

    private InputStream inputStream;

    private Request request;
    private Response response;

    private Map<String, List<String>> requestHeaderFields;
    private Map<String, List<String>> responseHeaderFields;

    @Before
    public void setUp() throws IOException {

        startLineParser = mock(StartLineParser.class);
        headerParser = mock(HeaderParser.class);
        instance = new MessageParserImpl(startLineParser, headerParser);

        inputStream = mock(InputStream.class);

        request = mock(Request.class);
        response = mock(Response.class);
        when(request.getHeaderFields()).thenReturn(spy(new HashMap<>()));
        when(response.getHeaderFields()).thenReturn(spy(new HashMap<>()));
        when(startLineParser.parseRequestLine(any())).thenReturn(request);
        when(startLineParser.parseStatusLine(any())).thenReturn(response);

        requestHeaderFields = spy(new HashMap<>());
        responseHeaderFields = spy(new HashMap<>());
        when(headerParser.parseRequestHeaders(any())).thenReturn(requestHeaderFields);
        when(headerParser.parseResponseHeaders(any())).thenReturn(responseHeaderFields);

    }

    // parseRequest:

    @Test
    public void parseRequest_usesStartLineParser() throws IOException {
        final Request result = instance.parseRequest(inputStream);
        verify(startLineParser).parseRequestLine(any());
        assertSame(request, result);
    }

    @Test
    public void parseRequest_usesHeaderParser() throws IOException {
        final Request result = instance.parseRequest(inputStream);
        verify(headerParser).parseRequestHeaders(any());
        verify(result.getHeaderFields()).putAll(requestHeaderFields);
    }

    // parseResponse:

    @Test
    public void parseResponse_usesStartLineParser() throws IOException {
        final Response result = instance.parseResponse(inputStream);
        verify(startLineParser).parseStatusLine(any());
        assertSame(response, result);
    }

    @Test
    public void parseResponse_usesHeaderParser() throws IOException {
        final Response result = instance.parseResponse(inputStream);
        verify(headerParser).parseResponseHeaders(any());
        verify(result.getHeaderFields()).putAll(responseHeaderFields);
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutStartLineParser_throwsNullPointerException() {
        new MessageParserImpl(null, headerParser);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHeaderParser_throwsNullPointerException() {
        new MessageParserImpl(startLineParser, null);
    }

    @Test(expected = NullPointerException.class)
    public void parseRequest_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.parseRequest(null);
    }

    @Test(expected = NullPointerException.class)
    public void parseResponse_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.parseResponse(null);
    }

}
