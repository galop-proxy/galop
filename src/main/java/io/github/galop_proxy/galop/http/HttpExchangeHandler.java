package io.github.galop_proxy.galop.http;

import java.net.Socket;

public interface HttpExchangeHandler {

    void handleRequest(Socket source, Socket target, Runnable startHandlingRequestCallback) throws Exception;

    void handleResponse(Socket source, Socket target, Runnable endHandlingResponseCallback) throws Exception;

}
