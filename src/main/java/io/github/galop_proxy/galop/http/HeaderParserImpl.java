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
    public Map<String, List<String>> parseRequestHeaders(final LineReader lineReader) throws IOException {
        return parseHeaderFields(lineReader, true);
    }

    @Override
    public Map<String, List<String>> parseResponseHeaders(final LineReader lineReader) throws IOException {
        return parseHeaderFields(lineReader, false);
    }

    private Map<String, List<String>> parseHeaderFields(final LineReader lineReader, final boolean request)
            throws IOException {

        final Map<String, List<String>> headerFields = new HashMap<>();

        String line;

        while (!(line = readFieldLine(lineReader, request)).isEmpty()) {
            checkFieldsLimit(headerFields, request);
            parseField(headerFields, line, request);
        }

        return processConnectionFields(headerFields);

    }

    private String readFieldLine(final LineReader lineReader, final boolean request) throws IOException {

        final int fieldSizeLimit;

        if (request) {
            fieldSizeLimit = requestConfiguration.getFieldSizeLimit();
        } else {
            fieldSizeLimit = responseConfiguration.getFieldSizeLimit();
        }

        try {
            return lineReader.readLine(fieldSizeLimit);
        } catch (final LineTooLargeException ex) {
            throw new HeaderFieldsTooLargeException(
                    "Maximum size of HTTP header field exceeded. "
                  + "A maximum size of " + fieldSizeLimit + " bytes is allowed.");
        }

    }

    private void checkFieldsLimit(final Map<String, List<String>> headerFields, final boolean request)
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

    private void parseField(final Map<String, List<String>> headerFields, final String line, final boolean request)
            throws InvalidHttpHeaderException {

        final int colonIndex = splitFieldLine(line);

        final String name = parseFieldName(line, colonIndex, request);
        final String value = parseFieldValue(line, colonIndex);

        addFieldToMessage(headerFields, name, value);

    }

    private int splitFieldLine(final String line) throws InvalidHttpHeaderException {

        final int colonIndex = line.indexOf(':');

        if (colonIndex == -1) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Missing colon.");
        }

        return colonIndex;

    }

    private String parseFieldName(final String line, final int colonIndex, final boolean request)
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

    private String parseFieldValue(final String line, final int colonIndex) {

        final String value;

        if (colonIndex + 1 < line.length()) {
            value = line.substring(colonIndex + 1).trim();
        } else {
            value = "";
        }

        return value;

    }

    private void addFieldToMessage(final Map<String, List<String>> headerFields, final String name, final String value) {

        if (!headerFields.containsKey(name)) {
            final List<String> values = new ArrayList<>();
            values.add(value);
            headerFields.put(name, values);
        } else {
            headerFields.get(name).add(value);
        }

    }

    private Map<String, List<String>> processConnectionFields(final Map<String, List<String>> headerFields) {

        if (headerFields.containsKey(HeaderFields.Request.CONNECTION)) {
            headerFields.keySet().removeAll(parseConnectionFieldValues(headerFields));
        }

        addConnectionField(headerFields);

        return headerFields;

    }

    private List<String> parseConnectionFieldValues(final Map<String, List<String>> headerFields) {

        final List<String> values = new ArrayList<>();

        for (final String headerField : headerFields.get(HeaderFields.Request.CONNECTION)) {

            for (final String value : headerField.split(",")) {
                values.add(value.trim().toLowerCase(Locale.ENGLISH));
            }

        }

        return values;

    }

    private void addConnectionField(final Map<String, List<String>> headerFields) {
        final List<String> connectionHeader = new ArrayList<>();
        connectionHeader.add("close");
        headerFields.put(HeaderFields.Request.CONNECTION, connectionHeader);
    }

}
