package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map.Entry;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;

final class HeaderWriterImpl implements HeaderWriter {

    @Override
    public void writeHeader(final Message message, final OutputStreamWriter outputStreamWriter) throws IOException {

        checkNotNull(message, "message");
        checkNotNull(outputStreamWriter, "outputStreamWriter");

        for (final Entry<String, List<String>> fields : message.getHeaderFields().entrySet()) {

            final String name = fields.getKey();
            final List<String> values = fields.getValue();

            for (final String value : values) {
                outputStreamWriter.write(name + ": " + value + NEW_LINE);
            }

        }

        outputStreamWriter.write(NEW_LINE);
        outputStreamWriter.flush();

    }

}
