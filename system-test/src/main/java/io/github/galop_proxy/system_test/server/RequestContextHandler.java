package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.handler.ContextHandler;

final class RequestContextHandler extends ContextHandler {

    RequestContextHandler() {
        super("/request");
        setHandler(new RequestHandler());
    }

}
