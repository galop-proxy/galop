package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.configuration.Configuration;

import java.net.Socket;

interface ConnectionHandlerFactory {

    ConnectionHandler create(Configuration configuration, Socket source, Socket target);

}
