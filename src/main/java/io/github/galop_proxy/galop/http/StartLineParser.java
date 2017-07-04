package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;

interface StartLineParser {

    Request parseRequestLine(Callable<String, IOException> nextLine) throws IOException;

    Response parseStatusLine(Callable<String, IOException> nextLine) throws IOException;

}
