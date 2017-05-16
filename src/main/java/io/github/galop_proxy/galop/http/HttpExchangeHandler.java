package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.configuration.Configuration;

import java.net.Socket;

public interface HttpExchangeHandler {

    void handleRequest(Socket source, Socket target, Configuration configuration, Runnable startHandlingRequestCallback)
            throws Exception;

    void handleResponse(Socket source, Socket target, Configuration configuration, Runnable endHandlingResponseCallback)
            throws Exception;

}
