package io.github.galop_proxy.system_test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

final class FileServer extends Server {

    FileServer(final int port) {

        super(port);

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("/"));
        setHandler(resourceHandler);

    }

}
