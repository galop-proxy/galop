package io.github.sebastianschmidt.galop.configuration;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Tests the class {@link ConfigurationPropertyKeys}.
 */
public class ConfigurationPropertyKeysTest {

    @Test(expected = Exception.class)
    public void construct_throwsException() throws Exception {
        final Constructor<ConfigurationPropertyKeys> constructor = ConfigurationPropertyKeys.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
