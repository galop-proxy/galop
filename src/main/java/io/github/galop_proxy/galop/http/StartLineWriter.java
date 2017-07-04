package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.Writer;

interface StartLineWriter {

    void writeRequestLine(Request request, Writer writer) throws IOException;

    void writeStatusLine(Response response, Writer writer) throws IOException;

}
