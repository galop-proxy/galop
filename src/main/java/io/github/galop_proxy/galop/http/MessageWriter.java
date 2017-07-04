package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface MessageWriter {

    void writeRequest(Request request, InputStream inputStream, OutputStream outputStream) throws IOException;

    void writeResponse(Response response, InputStream inputStream, OutputStream outputStream) throws IOException;

}
