package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.InputStream;

interface MessageParser {

    Request parseRequest(InputStream inputStream) throws IOException;

    Response parseResponse(InputStream inputStream) throws IOException;

}
