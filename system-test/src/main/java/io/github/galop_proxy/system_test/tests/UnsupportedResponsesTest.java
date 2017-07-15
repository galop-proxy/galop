package io.github.galop_proxy.system_test.tests;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class UnsupportedResponsesTest {

    private HttpClient client;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.start();
    }

    @After
    public void cleanUp() throws Exception {
        client.stop();
    }

    @Test
    public void The_status_code_101_is_rejected_with_status_code_502() throws Exception {
        final ContentResponse response = client.GET("http://localhost:8080/status-code/101");
        assertEquals(502, response.getStatus());
    }

    @Test
    public void The_status_code_426_is_rejected_with_status_code_502() throws Exception {
        final ContentResponse response = client.GET("http://localhost:8080/status-code/426");
        assertEquals(502, response.getStatus());
    }

}
