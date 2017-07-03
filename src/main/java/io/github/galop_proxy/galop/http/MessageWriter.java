package io.github.galop_proxy.galop.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface MessageWriter {

    void writeMessage(HttpHeaderParser.Result header, InputStream inputStream, OutputStream outputStream)
            throws IOException;

}
