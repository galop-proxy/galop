package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;

interface HeaderWriter {

    void writeHeader(Message message, OutputStreamWriter outputStreamWriter) throws IOException;

}
