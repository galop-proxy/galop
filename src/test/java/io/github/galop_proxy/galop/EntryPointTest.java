package io.github.galop_proxy.galop;

import org.junit.*;

import java.lang.reflect.Constructor;
import java.security.Permission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the class {@link EntryPoint}.
 */
public class EntryPointTest {

    private static class ExitException extends SecurityException {

        private final int status;

        private ExitException(final int status) {
            this.status = status;
        }

        private int getStatus() {
            return status;
        }

    }

    private static class NoExitSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(final Permission permission) {
            // Everything allowed.
        }

        @Override
        public void checkPermission(final Permission permission, final Object context) {
            // Everything allowed.
        }

        @Override
        public void checkExit(final int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }

    }

    @BeforeClass
    public static void setUp() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterClass
    public static void tearDown() {
        System.setSecurityManager(null);
    }

    @Test
    public void main_withoutArguments_exitsApplication() {
        try {
            EntryPoint.main(new String[0]);
            fail("System exit expected.");
        } catch (final ExitException ex) {
            assertEquals(1, ex.getStatus());
        }
    }

    @Test
    public void main_withHelpArgument_printsHelpAndReturns() {
        EntryPoint.main(new String[] { "-help" });
        EntryPoint.main(new String[] { "--help" });
    }

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<EntryPoint> constructor = EntryPoint.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
