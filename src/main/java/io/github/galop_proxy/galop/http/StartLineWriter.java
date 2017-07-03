package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.OutputStreamWriter;

interface StartLineWriter {

    void writeRequestLine(Request request, OutputStreamWriter outputStreamWriter) throws IOException;

    void writeStatusLine(Response response, OutputStreamWriter outputStreamWriter) throws IOException;

}
