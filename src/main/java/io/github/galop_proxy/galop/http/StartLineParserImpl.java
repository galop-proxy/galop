package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.commons.Callable;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;

import java.io.IOException;

import static io.github.galop_proxy.galop.http.HttpConstants.HTTP_VERSION_PREFIX;

final class StartLineParserImpl implements StartLineParser {

    @Override
    public Request parseRequestLine(final Callable<String, IOException> nextLine) throws IOException {

        final String[] requestLine = splitStartLine(nextLine);

        if (requestLine.length != 3) {
            throw new InvalidHttpHeaderException("Invalid request line.");
        }

        final String method = requestLine[0];
        final String requestTarget = requestLine[1];
        final Version version = parseVersion(requestLine[2]);

        return new RequestImpl(version, method, requestTarget);

    }

    @Override
    public Response parseStatusLine(final Callable<String, IOException> nextLine) throws IOException {

        final String[] statusLine = splitStartLine(nextLine);

        if (statusLine.length != 3 && statusLine.length != 2) {
            throw new InvalidHttpHeaderException("Invalid status line.");
        }

        final Version version = parseVersion(statusLine[0]);
        final int statusCode = parseStatusCode(statusLine[1]);
        final String reasonPhrase = parseReasonPhrase(statusLine);

        return new ResponseImpl(version, statusCode, reasonPhrase);

    }

    private String[] splitStartLine(final Callable<String, IOException> nextLine) throws IOException {
        return nextLine.call().split(" ");
    }

    private Version parseVersion(final String version) throws InvalidHttpHeaderException {

        if (isInvalidVersion(version)) {
            throw new InvalidHttpHeaderException("Invalid HTTP version in request line.");
        }

        final int major = Character.getNumericValue(version.charAt(5));
        final int minor = Character.getNumericValue(version.charAt(7));

        return new Version(major, minor);

    }

    private boolean isInvalidVersion(final String version) {
        // "HTTP/" DIGIT "." DIGIT
        return version.length() != 8
                || !version.startsWith(HTTP_VERSION_PREFIX)
                || !Character.isDigit(version.charAt(5))
                || version.charAt(6) != '.'
                || !Character.isDigit(version.charAt(7));
    }

    private int parseStatusCode(final String statusCode) throws InvalidHttpHeaderException {

        if (statusCode.length() != 3
                || !Character.isDigit(statusCode.charAt(0))
                || !Character.isDigit(statusCode.charAt(1))
                || !Character.isDigit(statusCode.charAt(2))) {
            throw new InvalidHttpHeaderException("Invalid status line: The status code must consist of three digits.");
        }

        return Integer.parseInt(statusCode);

    }

    private String parseReasonPhrase(final String[] statusLine) {

        if (statusLine.length == 3) {
            return statusLine[2];
        } else {
            return "";
        }

    }

}
