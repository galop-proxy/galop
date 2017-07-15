package io.github.galop_proxy.galop.http;

import java.net.Socket;

public interface ExchangeHandler {

    void handleRequest(Socket source, Socket target) throws Exception;

    void handleResponse(Socket source, Socket target) throws Exception;

}
