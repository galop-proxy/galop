package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class MessageParserImpl implements MessageParser {

    private final StartLineParser startLineParser;
    private final HeaderParser headerParser;

    @Inject
    MessageParserImpl(final StartLineParser startLineParser, final HeaderParser headerParser) {
        this.startLineParser = checkNotNull(startLineParser, "startLineParser");
        this.headerParser = checkNotNull(headerParser, "headerParser");
    }

    @Override
    public Request parseRequest(final InputStream inputStream) throws IOException {

        final LineReader lineReader = new LineReader(inputStream);

        final Request request = startLineParser.parseRequestLine(lineReader);
        request.getHeaderFields().putAll(headerParser.parseRequestHeaders(lineReader));
        return request;

    }

    @Override
    public Response parseResponse(final InputStream inputStream) throws IOException {

        final LineReader lineReader = new LineReader(inputStream);

        final Response response = startLineParser.parseStatusLine(lineReader);
        response.getHeaderFields().putAll(headerParser.parseResponseHeaders(lineReader));
        return response;

    }

}
