package io.github.galop_proxy.system_test;

import io.github.galop_proxy.system_test.server.TestServer;
import org.eclipse.jetty.server.Server;

class EntryPoint {

    public static void main(final String[] args) throws Exception {

        final Server server = new TestServer();
        server.start();

        final boolean successful = TestRunner.runTestsAndWait();

        server.stop();

        if (!successful) {
            System.exit(1);
        }

    }

}
