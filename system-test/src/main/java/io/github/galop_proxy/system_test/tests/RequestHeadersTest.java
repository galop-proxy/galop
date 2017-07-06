package io.github.galop_proxy.system_test.tests;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RequestHeadersTest {

    private HttpClient client;
    private ContentResponse response;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.start();
        response = client.newRequest("http://localhost:8080/request/")
                .method(HttpMethod.GET)
                .header("LOREM", "IpSuM")
                .send();
    }

    @After
    public void cleanUp() throws Exception {
        client.stop();
    }

    @Test
    public void Header_field_names_are_converted_to_lowercase_letters() {
        assertRequestHeader("lorem", "IpSuM");
    }

    @Test
    public void The_host_header_value_is_not_changed() {
        assertRequestHeader(HttpHeader.HOST.asString(), "localhost:8080");
    }

    private void assertRequestHeader(final String name, final String value) {
        final String header = "\r\n" + name + ": " + value + "\r\n";
        assertTrue(response.getContentAsString().contains(header));
    }

}
