package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;

import javax.inject.Inject;
import java.io.IOException;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.HTTP_VERSION_PREFIX;
import static io.github.galop_proxy.galop.http.Constants.SUPPORTED_HTTP_VERSION;

final class StartLineParserImpl implements StartLineParser {

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;

    @Inject
    StartLineParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                        final HttpHeaderResponseConfiguration responseConfiguration) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
    }

    @Override
    public Request parseRequestLine(final LineReader lineReader) throws IOException {

        final String[] requestLine = readRequestLine(lineReader);

        if (requestLine.length != 3) {
            throw new InvalidHttpHeaderException("Invalid request line.");
        }

        final String method = requestLine[0];
        final String requestTarget = requestLine[1];
        final Version version = parseVersion(requestLine[2]);

        return new RequestImpl(version, method, requestTarget);

    }

    @Override
    public Response parseStatusLine(final LineReader lineReader) throws IOException {

        final String[] statusLine = readStatusLine(lineReader);

        if (statusLine.length != 3 && statusLine.length != 2) {
            throw new InvalidHttpHeaderException("Invalid status line.");
        }

        final Version version = parseVersion(statusLine[0]);
        final int statusCode = parseStatusCode(statusLine[1]);
        final String reasonPhrase = parseReasonPhrase(statusLine);

        return new ResponseImpl(version, statusCode, reasonPhrase);

    }

    private String[] readRequestLine(final LineReader lineReader) throws IOException {
        final int sizeLimit = requestConfiguration.getRequestLineSizeLimit();
        return lineReader.readLine(sizeLimit).split(" ");
    }

    private String[] readStatusLine(final LineReader lineReader) throws IOException {
        final int sizeLimit = responseConfiguration.getStatusLineSizeLimit();
        return lineReader.readLine(sizeLimit).split(" ", 3);
    }

    private Version parseVersion(final String version) throws IOException {

        if (isInvalidVersion(version)) {
            throw new InvalidHttpHeaderException("Invalid HTTP version in request line.");
        }

        final int major = Character.getNumericValue(version.charAt(5));
        final int minor = Character.getNumericValue(version.charAt(7));

        final Version parsedVersion = new Version(major, minor);

        if (!parsedVersion.equals(SUPPORTED_HTTP_VERSION)) {
            throw new UnsupportedHttpVersionException(parsedVersion);
        }

        return parsedVersion;

    }

    private boolean isInvalidVersion(final String version) {
        // "HTTP/" DIGIT "." DIGIT
        return version.length() != 8
                || !version.startsWith(HTTP_VERSION_PREFIX)
                || !Character.isDigit(version.charAt(5))
                || version.charAt(6) != '.'
                || !Character.isDigit(version.charAt(7));
    }

    private int parseStatusCode(final String statusCode) throws IOException {

        if (isInvalidStatusCode(statusCode)) {
            throw new InvalidHttpHeaderException("Invalid status line: The status code must consist of three digits.");
        }

        final int parsedStatusCode = Integer.parseInt(statusCode);

        if (parsedStatusCode == StatusCode.UPGRADE_REQUIRED.getCode()
                || parsedStatusCode == StatusCode.SWITCHING_PROTOCOLS.getCode()) {
            throw new UnsupportedStatusCodeException(parsedStatusCode);
        }

        return parsedStatusCode;

    }

    private boolean isInvalidStatusCode(final String statusCode) {
        return statusCode.length() != 3
                || !Character.isDigit(statusCode.charAt(0))
                || !Character.isDigit(statusCode.charAt(1))
                || !Character.isDigit(statusCode.charAt(2));
    }

    private String parseReasonPhrase(final String[] statusLine) {

        if (statusLine.length == 3) {
            return statusLine[2];
        } else {
            return "";
        }

    }

}
