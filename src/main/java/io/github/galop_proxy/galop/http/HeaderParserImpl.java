package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.HeaderFields;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class HeaderParserImpl implements HeaderParser {

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;

    @Inject
    HeaderParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                     final HttpHeaderResponseConfiguration responseConfiguration) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
    }

    @Override
    public Map<String, List<String>> parseRequestHeaders(final Callable<String, IOException> nextLine)
            throws IOException {
        return parseHeaderFields(nextLine, true);
    }

    @Override
    public Map<String, List<String>> parseResponseHeaders(final Callable<String, IOException> nextLine)
            throws IOException {
        return parseHeaderFields(nextLine, false);
    }

    private Map<String, List<String>> parseHeaderFields(final Callable<String, IOException> nextLine, final boolean request)
            throws IOException {

        final Map<String, List<String>> headerFields = new HashMap<>();

        String line;

        while (!(line = nextLine.call()).isEmpty()) {
            checkHeaderFieldsLimit(headerFields, request);
            parseHeaderField(headerFields, line, request);
        }

        return processConnectionHeaderFields(headerFields);

    }

    private void checkHeaderFieldsLimit(final Map<String, List<String>> headerFields, final boolean request)
            throws HeaderFieldsTooLargeException {

        final int fieldsLimit;

        if (request) {
            fieldsLimit = requestConfiguration.getFieldsLimit();
        } else {
            fieldsLimit = responseConfiguration.getFieldsLimit();
        }

        if (headerFields.size() == fieldsLimit) {
            throw new HeaderFieldsTooLargeException(
                      "Maximum number of allowed HTTP header fields exceeded. "
                    + "A maximum of " + fieldsLimit + " fields are allowed.");
        }

    }

    private void parseHeaderField(final Map<String, List<String>> headerFields, final String line, final boolean request)
            throws InvalidHttpHeaderException {

        final int colonIndex = splitHeaderFieldLine(line);

        final String name = parseHeaderFieldName(line, colonIndex, request);
        final String value = parseHeaderFieldValue(line, colonIndex);

        addHeaderFieldToMessage(headerFields, name, value);

    }

    private int splitHeaderFieldLine(final String line) throws InvalidHttpHeaderException {

        final int colonIndex = line.indexOf(':');

        if (colonIndex == -1) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Missing colon.");
        }

        return colonIndex;

    }

    private String parseHeaderFieldName(final String line, final int colonIndex, final boolean request)
            throws InvalidHttpHeaderException {

        String name = line.substring(0, colonIndex).toLowerCase(Locale.ENGLISH);

        if (name.isEmpty()) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Empty header field name.");
        }

        if (Character.isWhitespace(name.charAt(name.length() - 1))) {

            if (request) {
                throw new InvalidHttpHeaderException("Invalid HTTP header field: "
                        + "No whitespace is allowed between the header field-name and colon.");
            } else {
                name = StringUtils.stripEnd(name, null);
            }

        }

        return name;

    }

    private String parseHeaderFieldValue(final String line, final int colonIndex) {

        final String value;

        if (colonIndex + 1 < line.length()) {
            value = line.substring(colonIndex + 1).trim();
        } else {
            value = "";
        }

        return value;

    }

    private void addHeaderFieldToMessage(final Map<String, List<String>> headerFields, final String name, final String value) {

        if (!headerFields.containsKey(name)) {
            final List<String> values = new ArrayList<>();
            values.add(value);
            headerFields.put(name, values);
        } else {
            headerFields.get(name).add(value);
        }

    }

    private Map<String, List<String>> processConnectionHeaderFields(final Map<String, List<String>> headerFields) {

        if (headerFields.containsKey(HeaderFields.Request.CONNECTION)) {
            final List<String> values = parseConnectionHeaderFields(headerFields);
            removeConnectionHeaderFields(headerFields, values);
        }

        return addConnectionHeaderField(headerFields);

    }

    private List<String> parseConnectionHeaderFields(final Map<String, List<String>> headerFields) {

        final List<String> values = new ArrayList<>();

        for (final String headerField : headerFields.get(HeaderFields.Request.CONNECTION)) {

            for (final String value : headerField.split(",")) {
                values.add(value.trim().toLowerCase(Locale.ENGLISH));
            }

        }

        return values;

    }

    private void removeConnectionHeaderFields(final Map<String, List<String>> headerFields, final List<String> values) {

        for (final String value : values) {
            headerFields.remove(value);
        }

    }

    private Map<String, List<String>> addConnectionHeaderField(final Map<String, List<String>> headerFields) {

        final List<String> connectionHeader = new ArrayList<>();
        connectionHeader.add("close");
        headerFields.put(HeaderFields.Request.CONNECTION, connectionHeader);

        return headerFields;

    }

}
