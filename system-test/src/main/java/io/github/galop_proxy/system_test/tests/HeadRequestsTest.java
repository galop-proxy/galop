package io.github.galop_proxy.system_test.tests;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeadRequestsTest {

    private HttpClient client;
    private ContentResponse response;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.start();
        response = client.newRequest("http://localhost:8080/hello-world.txt").method(HttpMethod.HEAD).send();
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
    public void The_message_body_is_empty() {
        assertEquals(0, response.getContent().length);
    }

}
