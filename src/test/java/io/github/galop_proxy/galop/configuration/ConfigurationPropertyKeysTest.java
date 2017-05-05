package io.github.galop_proxy.galop.configuration;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Tests the class {@link ConfigurationPropertyKeys}.
 */
public class ConfigurationPropertyKeysTest {

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<ConfigurationPropertyKeys> constructor = ConfigurationPropertyKeys.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
