package io.github.galop_proxy.galop.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link InvalidConfigurationException}.
 */
public class InvalidConfigurationExceptionTest {

    @Test
    public void missingProperty_returnsExceptionForMissingProperty() {
        final InvalidConfigurationException ex = InvalidConfigurationException.missingProperty("example");
        assertEquals("Property example is missing.", ex.getMessage());
    }

}
