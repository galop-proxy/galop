package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;

interface StartLineParser {

    Request parseRequestLine(LineReader lineReader) throws IOException;

    Response parseStatusLine(LineReader lineReader) throws IOException;

}
