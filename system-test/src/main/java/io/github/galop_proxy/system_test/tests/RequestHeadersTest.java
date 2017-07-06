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
                .header("hello", "world 1")
                .header("hello", "world 2")
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

    @Test
    public void Multiple_header_fields_with_the_same_name_are_passed_in_the_correct_order() {
        final String request = response.getContentAsString();
        assertRequestHeader("hello", "world 1");
        assertRequestHeader("hello", "world 2");
        assertTrue(request.indexOf("world 1") < request.indexOf("world 2"));
    }

    private void assertRequestHeader(final String name, final String value) {
        final String header = "\r\n" + name + ": " + value + "\r\n";
        assertTrue(response.getContentAsString().contains(header));
    }

}
