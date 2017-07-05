package io.github.galop_proxy.system_test;

import org.eclipse.jetty.server.Server;

class EntryPoint {

    public static void main(final String[] args) throws Exception {

        final Server server = new FileServer(3000);
        server.start();

        final boolean successful = TestRunner.runTestsAndWait();

        server.stop();

        if (!successful) {
            System.exit(1);
        }

    }

}
