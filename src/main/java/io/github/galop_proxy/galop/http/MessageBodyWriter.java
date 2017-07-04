package io.github.galop_proxy.galop.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface MessageBodyWriter {

    void writeIdentityEntity(InputStream inputStream, OutputStream outputStream, long length) throws IOException;

    void writeChunkedEntity(InputStream inputStream, OutputStream outputStream) throws IOException;

}
