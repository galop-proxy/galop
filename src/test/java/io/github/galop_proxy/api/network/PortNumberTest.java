package io.github.galop_proxy.api.network;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the class {@link PortNumber}.
 */
public class PortNumberTest {

    // Validate port number:

    @Test
    public void isValidPortNumber_withValidPortNumbers_returnsTrue() {
        assertTrue(PortNumber.isValidPortNumber(0));
        assertTrue(PortNumber.isValidPortNumber(80));
        assertTrue(PortNumber.isValidPortNumber(1024));
        assertTrue(PortNumber.isValidPortNumber(65535));
    }

    @Test
    public void isValidPortNumber_withInvalidPortNumbers_returnsFalse() {
        assertFalse(PortNumber.isValidPortNumber(-1));
        assertFalse(PortNumber.isValidPortNumber(65536));
    }

    // Parse port number:

    @Test
    public void parsePortNumber_withString_returnsPortNumber() {
        final PortNumber portNumber = PortNumber.parsePortNumber("443");
        assertNotNull(portNumber);
        assertEquals(443, portNumber.getValue());
    }

    @Test
    public void parsePortNumber_withNull_returnsNull() {
        assertNull(PortNumber.parsePortNumber(null));
    }

    @Test
    public void parsePortNumber_withInvalidInteger_returnsNull() {
        assertNull(PortNumber.parsePortNumber("abc"));
    }

    @Test
    public void parsePortNumber_withInvalidPortNumber_returnsNull() {
        assertNull(PortNumber.parsePortNumber("70000"));
    }

    // constructor:

    @Test
    public void constructor_withValidPortNumber_constructsObjectWithSpecifiedPortNumber() {
        final PortNumber portNumber = new PortNumber(8443);
        assertEquals(8443, portNumber.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withInvalidPortNumber_throwsIllegalArgumentException() {
        new PortNumber(-1);
    }

    // toString:

    @Test
    public void toString_returnsRawPortNumberAsString() {
        final PortNumber portNumber = new PortNumber(80);
        assertEquals("80", portNumber.toString());
    }

    // hashCode:

    @Test
    public void hashCode_fromTwoEqualPortNumbers_areEqual() {
        final PortNumber portNumber1 = new PortNumber(80);
        final PortNumber portNumber2 = new PortNumber(80);
        assertEquals(portNumber1.hashCode(), portNumber2.hashCode());
    }

    // equals:

    @Test
    public void equals_withPortNumberWithSameValue_returnsTrue() {
        final PortNumber portNumber1 = new PortNumber(80);
        final PortNumber portNumber2 = new PortNumber(80);
        assertTrue(portNumber1.equals(portNumber2));
        assertTrue(portNumber2.equals(portNumber1));
    }

    @Test
    public void equals_withPortNumberWithDifferentValue_returnsFalse() {
        final PortNumber portNumber1 = new PortNumber(80);
        final PortNumber portNumber2 = new PortNumber(443);
        assertFalse(portNumber1.equals(portNumber2));
        assertFalse(portNumber2.equals(portNumber1));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    public void equals_withSameObject_returnsTrue() {
        final PortNumber portNumber = new PortNumber(80);
        assertTrue(portNumber.equals(portNumber));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void equals_withNull_returnsFalse() {
        final PortNumber portNumber = new PortNumber(8080);
        assertFalse(portNumber.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void equals_withDifferentClass_returnsFalse() {
        final PortNumber portNumber = new PortNumber(443);
        assertFalse(portNumber.equals(443));
    }

}
