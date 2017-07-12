package io.github.galop_proxy.system_test.tests;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetRequestsTest {

    private HttpClient client;
    private ContentResponse response;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.start();
        response = client.GET("http://localhost:8080/static/hello-world.txt");
    }

    @After
    public void cleanUp() throws Exception {
        client.stop();
    }

    @Test
    public void The_status_code_200_is_returned() {
        assertEquals(200, response.getStatus());
    }

    @Test
    public void The_reason_phrase_OK_is_returned() {
        assertEquals("OK", response.getReason());
    }

    @Test
    public void The_content_is_passed_in_the_message_body() {
        assertEquals("Hello world!", response.getContentAsString());
    }

    @Test
    public void The_response_HTTP_version_is_1_1() {
        assertEquals(HttpVersion.HTTP_1_1, response.getVersion());
    }

}
