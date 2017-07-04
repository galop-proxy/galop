package io.github.galop_proxy.galop.http;

import com.google.inject.Inject;
import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.api.http.HeaderFields.Response.CONTENT_LENGTH;
import static io.github.galop_proxy.api.http.HeaderFields.Response.TRANSFER_ENCODING;
import static io.github.galop_proxy.galop.http.Constants.HEADER_CHARSET;

final class MessageWriterImpl implements MessageWriter {

    private final StartLineWriter startLineWriter;
    private final HeaderWriter headerWriter;
    private final MessageBodyWriter messageBodyWriter;

    @Inject
    MessageWriterImpl(final StartLineWriter startLineWriter, final HeaderWriter headerWriter,
                      final MessageBodyWriter messageBodyWriter) {
        this.startLineWriter = checkNotNull(startLineWriter, "startLineWriter");
        this.headerWriter = checkNotNull(headerWriter, "headerWriter");
        this.messageBodyWriter = checkNotNull(messageBodyWriter, "messageBodyWriter");
    }

    @Override
    public void writeRequest(final Request request, final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        checkNotNull(request, "request");
        writeMessage(request, true, inputStream, outputStream);
    }

    @Override
    public void writeResponse(final Response response, final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        checkNotNull(response, "response");
        writeMessage(response, false, inputStream, outputStream);
    }

    private void writeMessage(final Message message, final boolean request, final InputStream inputStream,
                              final OutputStream outputStream) throws IOException {

        checkNotNull(inputStream, "inputStream");
        checkNotNull(outputStream, "outputStream");

        final boolean chunkedEncoding = isChunkedEncoding(message);
        final long contentLength = parseContentLength(message, chunkedEncoding);

        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, HEADER_CHARSET);
        writeStartLine(message, request, outputStreamWriter);
        writeHeader(message, outputStreamWriter);

        if (chunkedEncoding) {
            messageBodyWriter.writeChunkedEntity(inputStream, outputStream);
        } else {
            messageBodyWriter.writeIdentityEntity(inputStream, outputStream, contentLength);
        }

    }

    private boolean isChunkedEncoding(final Message message) throws IOException {

        if (!message.getHeaderFields().containsKey(TRANSFER_ENCODING)) {
            return false;
        }

        final List<String> transferEncodings = message.getHeaderFields().get(TRANSFER_ENCODING);

        if (transferEncodings.size() == 0) {
            return false;
        }

        final String lastTransferEncoding = getLastTransferEncoding(transferEncodings);

        switch (lastTransferEncoding) {
            case Constants.TRANSFER_ENCODING_CHUNKED:
                return true;
            case Constants.TRANSFER_ENCODING_IDENTITY:
                return false;
            case "":
                return false;
            default:
                throw new UnsupportedTransferEncodingException(lastTransferEncoding);
        }

    }

    private String getLastTransferEncoding(final List<String> values) {

        final String value = values.get(values.size() - 1);
        final String[] transferEncodings = value.split(",");

        if (transferEncodings.length == 1) {
            return transferEncodings[0];
        } else {
            return transferEncodings[transferEncodings.length - 1].trim();
        }

    }

    private long parseContentLength(final Message message, final boolean chunkedEncoding) throws IOException {

        if (chunkedEncoding) {
            return 0;
        }

        if (!message.getHeaderFields().containsKey(CONTENT_LENGTH)) {
            return 0;
        }

        final List<String> contentLengths = message.getHeaderFields().get(CONTENT_LENGTH);

        if (contentLengths.size() == 0) {
            return 0;
        }

        final String lastContentLength = contentLengths.get(contentLengths.size() - 1);

        if (lastContentLength.isEmpty()) {
            return 0;
        }

        final long contentLength;

        try {
            contentLength = Long.parseLong(lastContentLength);
        } catch (final NumberFormatException ex) {
            throw new InvalidHttpHeaderException("Invalid Content-Length: Not a valid number.");
        }

        if (contentLength < 0) {
            throw new InvalidHttpHeaderException("Invalid Content-Length: Must be greater or equal to 0.");
        }

        return contentLength;

    }

    private void writeStartLine(final Message message, final boolean request, final OutputStreamWriter outputStream)
            throws IOException {

        if (request) {
            startLineWriter.writeRequestLine((Request) message, outputStream);
        } else {
            startLineWriter.writeStatusLine((Response) message, outputStream);
        }

    }

    private void writeHeader(final Message message, final OutputStreamWriter outputStream) throws IOException {
        headerWriter.writeHeader(message, outputStream);
    }

}
