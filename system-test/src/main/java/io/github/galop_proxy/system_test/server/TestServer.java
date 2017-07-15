package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.io.IOException;

public final class TestServer extends Server {

    public TestServer() throws IOException {

        super(3000);

        final ContextHandlerCollection contexts = new ContextHandlerCollection();

        contexts.setHandlers(new Handler[]{
                new StaticFilesContextHandler(),
                new RequestContextHandler(),
                new ChunkedContextHandler(),
                new StatusCodeContextHandler()
        });

        setHandler(contexts);

    }

    public static void main(String... args) throws Exception {
        final Server server = new TestServer();
        server.start();
        server.join();
    }

}
