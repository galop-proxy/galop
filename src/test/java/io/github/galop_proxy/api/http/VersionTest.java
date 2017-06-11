package io.github.galop_proxy.api.http;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the class {@link Version}.
 */
public class VersionTest {

    private Version v10;
    private Version v11;
    private Version v20;
    private Version other;

    @Before
    public void setUp() {
        v10 = new Version(1, 0);
        v11 = new Version(1, 1);
        v20 = new Version(2, 0);
        other = new Version(1, 1);
    }

    // Constructor:

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNegativeMajorNumber_throwsIllegalArgumentException() {
        new Version(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNegativeMinorNumber_throwsIllegalArgumentException() {
        new Version(1, -1);
    }

    // Getter:

    @Test
    public void getMajor_returnsGivenMajorNumber() {
        assertEquals(1, v10.getMajor());
    }

    @Test
    public void getMinor_returnsGivenMinorNumber() {
        assertEquals(1, v11.getMinor());
    }

    // isGreaterThan:

    @Test
    public void isGreaterThan_withLowerMajorNumber_returnsTrue() {
        assertTrue(v20.isGreaterThan(v10));
    }

    @Test
    public void isGreaterThan_withLowerMajorAndHigherMinorNumber_returnsTrue() {
        assertTrue(v20.isGreaterThan(v11));
    }

    @Test
    public void isGreater_withSameMajorAndLowerMinorNumber_returnsTrue() {
        assertTrue(v11.isGreaterThan(v10));
    }

    @Test
    public void isGreaterThan_withHigherMajorNumber_returnsFalse() {
        assertFalse(v10.isGreaterThan(v20));
    }

    @Test
    public void isGreaterThan_withSameMajorAndHigherMinorNumber_returnsFalse() {
        assertFalse(v10.isGreaterThan(v11));
    }

    @Test
    public void isGreaterThan_withSameMajorAndSameMinorNumber_returnsFalse() {
        assertFalse(v11.isGreaterThan(v11));
    }

    // isLowerThan:

    @Test
    public void isLowerThan_withHigherMajorNumber_returnsTrue() {
        assertTrue(v10.isLowerThan(v20));
    }

    @Test
    public void isLowerThan_withSameMajorAndHigherMinorNumber_returnsTrue() {
        assertTrue(v10.isLowerThan(v11));
    }

    @Test
    public void isLowerThan_withHigherMajorNumberAndLowerMinorNumber_returnsTrue() {
        assertTrue(v11.isLowerThan(v20));
    }

    @Test
    public void isLowerThan_withLowerMajorNumber_returnsFalse() {
        assertFalse(v20.isLowerThan(v10));
    }

    @Test
    public void isLowerThan_withLowerMajorNumberAndHigherMinorNumber_returnsFalse() {
        assertFalse(v20.isLowerThan(v11));
    }

    @Test
    public void isLowerThan_withSameMajorAndLowerMinorNumber_returnsFalse() {
        assertFalse(v11.isLowerThan(v10));
    }

    @Test
    public void isLowerThan_withSameMajorAndSameMinorNumber_returnsFalse() {
        assertFalse(v11.isLowerThan(v11));
    }

    // compareTo:

    @Test
    public void compareTo_withSameMajorAndMinorNumbers_returnsZero() {
        assertEquals(0, v11.compareTo(other));
        assertTrue(v11.equals(other));
    }

    @Test
    public void compareTo_withLowerVersionNumber_returnsPositiveValue() {
        assertTrue(v11.compareTo(v10) > 0);
    }

    @Test
    public void compareTo_withHigherVersionNumber_returnsNegativeValue() {
        assertTrue(v11.compareTo(v20) < 0);
    }

    // equals:

    @Test
    public void equals_withSameMajorAndMinorNumbers_returnsTrue() {
        assertTrue(v11.equals(other));
    }

    @Test
    public void equals_withDifferentMajorNumbers_returnsFalse() {
        assertFalse(v10.equals(v20));
    }

    @Test
    public void equals_withDifferentMinorNumbers_returnsFalse() {
        assertFalse(v11.equals(v10));
    }
    
    @Test
    @SuppressWarnings("EqualsWithItself")
    public void equals_withSameObject_returnsTrue() {
        assertTrue(v11.equals(v11));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void equals_withNull_returnsFalse() {
        assertFalse(v11.equals(null));
    }
    
    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void equals_withDifferentClass_returnsFalse() {
        assertFalse(v11.equals("1.1"));
    }

    // hashCode:

    @Test
    public void hashCode_fromEqualObjects_returnsSameValue() {
        assertEquals(v11.hashCode(), other.hashCode());
    }

    // toString:

    @Test
    public void toString_returnConcatenatedMajorAndMinorNumbers() {
        assertEquals("1.1", v11.toString());
    }

}
