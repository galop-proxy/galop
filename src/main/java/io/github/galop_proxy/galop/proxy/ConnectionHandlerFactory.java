package io.github.galop_proxy.galop.proxy;

import java.net.Socket;

interface ConnectionHandlerFactory {

    ConnectionHandler create(Socket source, Socket target);

}
