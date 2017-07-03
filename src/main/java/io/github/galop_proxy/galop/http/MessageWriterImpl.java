package io.github.galop_proxy.galop.http;

import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class MessageWriterImpl implements MessageWriter {

    private final MessageBodyWriter messageBodyWriter;

    @Inject
    MessageWriterImpl(final MessageBodyWriter messageBodyWriter) {
        this.messageBodyWriter = checkNotNull(messageBodyWriter, "messageBodyWriter");
    }

    @Override
    public void writeMessage(final HttpHeaderParser.Result header, final InputStream inputStream,
                             final OutputStream outputStream) throws IOException {

        checkNotNull(header, "header");
        checkNotNull(inputStream, "inputStream");
        checkNotNull(outputStream, "outputStream");

        if (header.isChunkedTransferEncoding()) {
            handleChunkedTransferEncoding(header, inputStream, outputStream);
        } else {
            handleIdentityTransferEncoding(header, inputStream, outputStream);
        }

    }

    private void handleIdentityTransferEncoding(final HttpHeaderParser.Result header, final InputStream inputStream,
                                                final OutputStream outputStream) throws IOException {
        final long bodyLength = header.getTotalLength() - header.getHeaderLength();
        IOUtils.copyLarge(inputStream, outputStream, 0, header.getHeaderLength());
        messageBodyWriter.writeIdentityEntity(inputStream, outputStream, bodyLength);
    }

    private void handleChunkedTransferEncoding(final HttpHeaderParser.Result header, final InputStream inputStream,
                                               final OutputStream outputStream) throws IOException {
        IOUtils.copyLarge(inputStream, outputStream, 0, header.getHeaderLength());
        messageBodyWriter.writeChunkedEntity(inputStream, outputStream);
    }

}
