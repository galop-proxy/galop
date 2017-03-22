package io.github.sebastianschmidt.galop.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HttpMessageHandler {

    void handle(HttpHeaderParser.Result header, InputStream inputStream, OutputStream outputStream) throws IOException;

}
