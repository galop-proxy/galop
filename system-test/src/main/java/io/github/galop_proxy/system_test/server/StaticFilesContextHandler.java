package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

final class StaticFilesContextHandler extends ContextHandler {

    StaticFilesContextHandler() {

        super("/static");

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("files"));
        setHandler(resourceHandler);

    }

}
