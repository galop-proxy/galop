package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public final class FileServer extends Server {

    public FileServer() {

        super(3000);

        final ContextHandlerCollection contexts = new ContextHandlerCollection();

        contexts.setHandlers(new Handler[]{
                new StaticFilesContextHandler(),
                new RequestContextHandler()
        });

        setHandler(contexts);

    }

}
