package io.github.galop_proxy.system_test.tests;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChunkedTransferEncodingTest {

    private HttpClient client;
    private ContentResponse response;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.start();
        response = client.GET("http://localhost:8080/chunked/");
    }

    @After
    public void cleanUp() throws Exception {
        client.stop();
    }

    @Test
    public void The_transfer_encoding_chunked_was_used() {
        final String[] transferEncodings = getTransferEncodings();
        final String lastTransferEncoding = transferEncodings[transferEncodings.length - 1];
        assertEquals("chunked", lastTransferEncoding);
    }

    @Test
    public void The_file_has_been_transferred_completely_and_correctly() throws IOException {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("files/example.png");
        final byte[] file = IOUtils.toByteArray(inputStream);
        assertArrayEquals(file, response.getContent());
    }

    @Test
    public void Other_transfer_encodings_have_been_ignored_and_not_changed() {
        assertTrue(getTransferEncodings().length > 1);
    }

    private String[] getTransferEncodings() {

        final String[] transferEncodings = response.getHeaders().get("transfer-encoding").split(",");

        for (int index = 0; index < transferEncodings.length; index++) {
            transferEncodings[index] = transferEncodings[index].trim();
        }

        return transferEncodings;

    }

}
