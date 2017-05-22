package io.github.galop_proxy.galop.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link InvalidChunkException}.
 */
public class InvalidChunkExceptionTest {

    @Test
    public void getMessage_returnsGivenErrorMessage() {
        final String message = "Hello, world!";
        final InvalidChunkException ex = new InvalidChunkException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void getCause_returnsGivenException() {
        final RuntimeException cause = new RuntimeException();
        final InvalidChunkException ex = new InvalidChunkException("Hello, world!", cause);
        assertSame(cause, ex.getCause());
    }

}
