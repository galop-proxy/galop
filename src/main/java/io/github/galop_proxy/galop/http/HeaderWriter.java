package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;

import java.io.IOException;
import java.io.Writer;

interface HeaderWriter {

    void writeHeader(Message message, Writer writer) throws IOException;

}
