package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.HeaderFields;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link HeaderParserImpl}.
 */
public class HeaderParserImplTest {

    private static final String REQUEST_HOST = "localhost";
    private static final String REQUEST_USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:54.0) Gecko/20100101 Firefox/54.0";
    private static final String REQUEST_X_FORWARDED_FOR_1 = "1.1.1.1";
    private static final String REQUEST_X_FORWARDED_FOR_2 = "2.2.2.2";

    private static final String RESPONSE_SERVER = "GALOP";
    private static final String RESPONSE_CONTENT_TYPE = "text/html; charset=utf-8";
    private static final String RESPONSE_SET_COOKIE_1 = "sessionId=123456789; HttpOnly; Path=/";
    private static final String RESPONSE_SET_COOKIE_2 = "lorem=ipsum; HttpOnly; Path=/";

    private static final List<String> REQUEST = Arrays.asList(
            "Host: " + REQUEST_HOST,
            "User-Agent: " + REQUEST_USER_AGENT,
            "Cookie:", // Empty value
            "X-Forwarded-For: " + REQUEST_X_FORWARDED_FOR_1,
            "X-Forwarded-For: " + REQUEST_X_FORWARDED_FOR_2,
            "Connection: upgrade, lorem",
            "Upgrade: HTTP/2.0",
            "Lorem: Ipsum");

    private static final List<String> RESPONSE = Arrays.asList(
            "Server: " + RESPONSE_SERVER,
            "Content-Type: " + RESPONSE_CONTENT_TYPE,
            "Date:", // Empty value
            "Set-Cookie: " + RESPONSE_SET_COOKIE_1,
            "Set-Cookie: " + RESPONSE_SET_COOKIE_2,
            "Connection: upgrade, lorem",
            "Upgrade: HTTP/2.0",
            "Lorem: Ipsum");

    private HttpHeaderRequestConfiguration requestConfiguration;
    private HttpHeaderResponseConfiguration responseConfiguration;
    private HeaderParser instance;

    private Map<String, List<String>> request;
    private Map<String, List<String>> response;

    @Before
    public void setUp() throws IOException {

        requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        when(requestConfiguration.getFieldsLimit()).thenReturn(20);
        when(requestConfiguration.getFieldSizeLimit()).thenReturn(256);

        responseConfiguration = mock(HttpHeaderResponseConfiguration.class);
        when(responseConfiguration.getFieldsLimit()).thenReturn(20);
        when(responseConfiguration.getFieldSizeLimit()).thenReturn(256);

        instance = new HeaderParserImpl(requestConfiguration, responseConfiguration);

        request = instance.parseRequestHeaders(create(REQUEST));
        response = instance.parseResponseHeaders(create(RESPONSE));

    }

    // parseRequestHeaders:

    @Test
    public void parseRequestHeaders_withSimpleHeaderFieldValue_returnsParsedValue() {
        assertHeaderField(request, HeaderFields.Request.HOST, REQUEST_HOST);
    }

    @Test
    public void parseRequestHeaders_withComplexHeaderFieldValue_returnsParsedValue() {
        assertHeaderField(request, HeaderFields.Request.USER_AGENT, REQUEST_USER_AGENT);
    }

    @Test
    public void parseRequestHeaders_withEmptyHeaderFieldValue_returnsEmptyValue() {
        assertHeaderField(request, HeaderFields.Request.COOKIE, "");
    }

    @Test
    public void parseRequestHeaders_withMultipleHeaderFieldsWithTheSameName_returnsParsedValuesAsList() {
        assertHeaderField(request, HeaderFields.Request.X_FORWARDED_FOR,
                REQUEST_X_FORWARDED_FOR_1, REQUEST_X_FORWARDED_FOR_2);
    }

    @Test
    public void parseRequestsHeaders_withConnectionHeaderField_changesConnectionHeaderFieldToClose() {
        assertHeaderField(request, HeaderFields.Request.CONNECTION, "close");
    }

    @Test
    public void parseRequestsHeaders_withValueUpgradeInConnectionHeaderField_removesUpgradeHeaderField() {
        assertFalse(request.containsKey(HeaderFields.Request.UPGRADE));
    }

    @Test
    public void parseRequestsHeaders_withValueLoremInConnectionHeaderField_removesLoremHeaderField() {
        assertFalse(request.containsKey("lorem"));
    }

    @Test
    public void parseRequestHeaders_withoutHeaderFields_returnsOnlyConnectionHeaderField() throws IOException {
        final Map<String, List<String>> headerFields = instance.parseRequestHeaders(create(""));
        assertEquals(1, headerFields.size());
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestHeaders_withWhitespaceBetweenHeaderFieldNameAndColon_throwsInvalidHttpHeaderException()
            throws IOException {
        instance.parseRequestHeaders(create("Host :invalid"));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestHeaders_withHeaderFieldWithoutColon_throwsInvalidHttpHeaderException() throws IOException {
        instance.parseRequestHeaders(create("WithoutColon"));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseRequestHeaders_withEmptyHeaderFieldName_throwsInvalidHttpHeaderException() throws IOException {
        instance.parseRequestHeaders(create(": EmptyFieldName"));
    }

    @Test(expected = HeaderFieldsTooLargeException.class)
    public void parseRequestHeaders_withTooManyHeaderFields_throwsHeaderFieldsTooLargeException() throws IOException {
        instance.parseRequestHeaders(createWithTooManyLines());
    }

    @Test(expected = HeaderFieldsTooLargeException.class)
    public void parseRequestHeaders_withTooLargeHeaderField_throwsHeaderFieldsTooLargeException() throws IOException {
        instance.parseRequestHeaders(create("Long: " + StringUtils.repeat("a", 256)));
    }

    // parseResponseHeaders:

    @Test
    public void parseResponseHeaders_withSimpleHeaderFieldValue_returnsParsedValue() {
        assertHeaderField(response, HeaderFields.Response.SERVER, RESPONSE_SERVER);
    }

    @Test
    public void parseResponseHeaders_withComplexHeaderFieldValue_returnsParsedValue() {
        assertHeaderField(response, HeaderFields.Response.CONTENT_TYPE, RESPONSE_CONTENT_TYPE);
    }

    @Test
    public void parseResponseHeaders_withEmptyHeaderFieldValue_returnsEmptyValue() {
        assertHeaderField(response, HeaderFields.Response.DATE, "");
    }

    @Test
    public void parseResponseHeaders_withMultipleHeaderFieldsWithTheSameName_returnsParsedValuesAsList() {
        assertHeaderField(response, HeaderFields.Response.SET_COOKIE,
                RESPONSE_SET_COOKIE_1, RESPONSE_SET_COOKIE_2);
    }

    @Test
    public void parseResponseHeaders_withWhitespaceBetweenHeaderFieldNameAndColon_removesWhiteSpace() throws IOException {
       final Map<String, List<String>> response = instance.parseResponseHeaders(create("Server : " + RESPONSE_SERVER));
       assertHeaderField(response, HeaderFields.Response.SERVER, RESPONSE_SERVER);
    }

    @Test
    public void parseResponseHeaders_withoutHeaderFields_returnsOnlyConnectionHeaderField() throws IOException {
        final Map<String, List<String>> headerFields = instance.parseResponseHeaders(create(""));
        assertEquals(1, headerFields.size());
    }

    @Test
    public void parseResponseHeaders_withConnectionHeaderField_changesConnectionHeaderFieldToClose() {
        assertHeaderField(response, HeaderFields.Request.CONNECTION, "close");
    }

    @Test
    public void parseResponseHeaders_withValueUpgradeInConnectionHeaderField_removesUpgradeHeaderField() {
        assertFalse(response.containsKey(HeaderFields.Request.UPGRADE));
    }

    @Test
    public void parseResponseHeaders_withValueLoremInConnectionHeaderField_removesLoremHeaderField() {
        assertFalse(response.containsKey("lorem"));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponseHeaders_withHeaderFieldWithoutColon_throwsInvalidHttpHeaderException() throws IOException {
        instance.parseResponseHeaders(create("WithoutColon"));
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void parseResponseHeaders_withEmptyHeaderFieldName_throwsInvalidHttpHeaderException() throws IOException {
        instance.parseResponseHeaders(create(": EmptyFieldName"));
    }

    @Test(expected = HeaderFieldsTooLargeException.class)
    public void parseResponseHeaders_withTooManyHeaderFields_throwsHeaderFieldsTooLargeException() throws IOException {
        instance.parseResponseHeaders(createWithTooManyLines());
    }

    @Test(expected = HeaderFieldsTooLargeException.class)
    public void parseResponseHeaders_withTooLargeHeaderField_throwsHeaderFieldsTooLargeException() throws IOException {
        instance.parseResponseHeaders(create("Long: " + StringUtils.repeat("a", 256)));
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderRequestConfiguration_throwsNullPointerException() {
        new HeaderParserImpl(null, responseConfiguration);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderResponseConfiguration_throwsNullPointerException() {
        new HeaderParserImpl(requestConfiguration, null);
    }

    @Test(expected = NullPointerException.class)
    public void parseRequestHeaders_withoutCallable_throwsNullPointerException() throws IOException {
        instance.parseRequestHeaders(null);
    }

    @Test(expected = NullPointerException.class)
    public void parseResponseHeaders_withoutCallable_throwsNullPointerException() throws IOException {
        instance.parseResponseHeaders(null);
    }

    // Helper methods:

    private LineReader create(final List<String> lines) {

        final StringBuilder concatenatedLines = new StringBuilder();

        for (final String line : lines) {
            concatenatedLines.append(line).append("\r\n");
        }

        concatenatedLines.append("\r\n");

        return new LineReader(new ByteArrayInputStream(concatenatedLines.toString().getBytes()));

    }

    private LineReader create(final String line) {
        return create(Collections.singletonList(line));
    }

    private LineReader createWithTooManyLines() {

        final List<String> tooManyLines = new ArrayList<>();

        for (int i = 0; i < 21; i++) {
            tooManyLines.add("example" + i + ": " + i);
        }

        return create(tooManyLines);

    }

    private void assertHeaderField(final Map<String, List<String>> headerFields, final String name,
                                   final String... expectedValue) {

        assertTrue(headerFields.containsKey(name));

        assertEquals(expectedValue.length, headerFields.get(name).size());

        for (int index = 0; index < expectedValue.length; index++) {
            assertEquals(expectedValue[index], headerFields.get(name).get(index));
        }

    }

}
