package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.handler.ContextHandler;

final class StatusCodeContextHandler extends ContextHandler {

    StatusCodeContextHandler() {
        super("/status-code");
        setHandler(new StatusCodeHandler());
    }

}
