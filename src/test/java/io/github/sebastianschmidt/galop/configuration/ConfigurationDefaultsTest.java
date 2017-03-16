package io.github.sebastianschmidt.galop.configuration;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Tests the class {@link ConfigurationDefaults}.
 */
public class ConfigurationDefaultsTest {

    @Test(expected = Exception.class)
    public void construct_throwsException() throws Exception {
        final Constructor<ConfigurationDefaults> constructor = ConfigurationDefaults.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
