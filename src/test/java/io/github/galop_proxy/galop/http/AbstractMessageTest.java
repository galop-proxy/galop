package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Version;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the class {@link AbstractMessage}.
 */
public class AbstractMessageTest {

    private Version version;
    private Message instance;

    @Before
    public void setUp() {
        version = new Version(1, 1);
        instance = new AbstractMessage(version) {};
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutVersion_throwsNullPointerException() {
        new AbstractMessage(null) {};
    }

    // getVersion:

    @Test
    public void getVersion_afterObjectCreation_returnGivenVersion() {
        assertSame(version, instance.getVersion());
    }

    // setVersion:

    @Test
    public void setVersion_withVersion_overwritesInitialVersion() {
        final Version otherVersion = new Version(1, 0);
        instance.setVersion(otherVersion);
        assertNotEquals(version, otherVersion);
        assertSame(otherVersion, instance.getVersion());
    }

    @Test(expected = NullPointerException.class)
    public void setVersion_withoutVersion_throwsNullPointerException() {
        instance.setVersion(null);
    }

    // getHeaders:

    @Test
    public void getHeaders_afterObjectCreation_returnsEmptyMap() {
        assertTrue(instance.getHeaders().isEmpty());
    }

    @Test
    public void getHeaders_returnsModifiableMap() {
        final String name = "hello";
        final List<String> values = Collections.singletonList("world");
        instance.getHeaders().put(name, values);
        assertEquals(values, instance.getHeaders().get(name));
    }

}
