package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;

final class HeaderWriterImpl implements HeaderWriter {

    @Override
    public void writeHeader(final Message message, final Writer writer) throws IOException {

        checkNotNull(message, "message");
        checkNotNull(writer, "writer");

        for (final Entry<String, List<String>> fields : message.getHeaderFields().entrySet()) {

            final String name = fields.getKey();
            final List<String> values = fields.getValue();

            for (final String value : values) {
                writer.write(name + ": " + value + NEW_LINE);
            }

        }

        writer.write(NEW_LINE);
        writer.flush();

    }

}
