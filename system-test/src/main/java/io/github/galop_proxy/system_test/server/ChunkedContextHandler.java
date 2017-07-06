package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.handler.ContextHandler;

import java.io.IOException;

final class ChunkedContextHandler extends ContextHandler {

    ChunkedContextHandler() throws IOException {
        super("/chunked");
        setHandler(new ChunkedHandler());
    }

}