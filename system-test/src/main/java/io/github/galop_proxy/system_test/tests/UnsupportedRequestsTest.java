package io.github.galop_proxy.system_test.tests;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnsupportedRequestsTest {

    private HttpClient client;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.setRequestBufferSize(16384); // Allow large request header fields.
        client.start();
    }

    @After
    public void cleanUp() throws Exception {
        client.stop();
    }

    @Test
    public void Requests_with_an_unknown_transfer_encoding_are_rejected_with_the_error_code_411() throws Exception {
        final ContentResponse response = client
                .newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .header("transfer-encoding", "unknown")
                .send();
        assertEquals(411, response.getStatus());
    }

    @Test
    public void Requests_with_too_large_header_fields_are_rejected_with_the_error_code_431() throws Exception {
        final ContentResponse response = client
                .newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .header("long", StringUtils.repeat("long", 3000))
                .send();
        assertEquals(431, response.getStatus());
    }

    @Test
    public void Requests_with_HTTP_version_1_0_are_rejected_with_the_error_code_505() throws Exception {
        final ContentResponse response = client
                .newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .version(HttpVersion.HTTP_1_0)
                .send();
        assertEquals(505, response.getStatus());
    }

    @Test
    public void Requests_with_HTTP_version_2_0_are_rejected_with_the_error_code_505() throws Exception {
        final ContentResponse response = client
                .newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .version(HttpVersion.HTTP_2)
                .send();
        assertEquals(505, response.getStatus());
    }

}
