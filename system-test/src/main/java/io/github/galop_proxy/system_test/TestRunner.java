package io.github.galop_proxy.system_test;

import io.github.galop_proxy.system_test.tests.GetRequestsTest;
import io.github.galop_proxy.system_test.tests.HeadRequestsTest;
import org.junit.runner.JUnitCore;

final class TestRunner {

    private final static Class<?>[] TEST_CLASSES = {
            GetRequestsTest.class,
            HeadRequestsTest.class
    };

    static boolean runTestsAndWait() {
        final JUnitCore runner = new JUnitCore();
        runner.addListener(new ExecutionListener());
        return runner.run(TEST_CLASSES).wasSuccessful();
    }

}
