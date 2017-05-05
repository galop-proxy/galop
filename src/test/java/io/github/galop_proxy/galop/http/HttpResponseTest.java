package io.github.galop_proxy.galop.http;

import org.junit.Test;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the class {@link HttpResponse}.
 */
public class HttpResponseTest {

    @Test
    public void build_withDateTimeAndWithoutContent_returnsResponseWithDefaultContent() {

        final ZonedDateTime dateTime = ZonedDateTime.of(2017, 3, 20, 19, 9, 3, 40, ZoneId.of("GMT"));
        final HttpResponse httpResponse = HttpResponse
                .createWithStatus(HttpStatusCode.BAD_REQUEST)
                .dateTime(dateTime);

        final Charset expectedCharset = Charset.forName("UTF-8");
        final String expectedDefaultContent = HttpStatusCode.BAD_REQUEST.getReason();
        final byte[] expectedDefaultContentBytes = expectedDefaultContent.getBytes(expectedCharset);
        final String expectedHttpResponse = "HTTP/1.1 400 Bad Request\r\n"
                + "date: Mon, 20 Mar 2017 19:09:03 GMT\r\n"
                + "content-length: " + expectedDefaultContentBytes.length + "\r\n"
                + "content-type: text/plain; charset=UTF-8\r\n"
                + "\r\n"
                + expectedDefaultContent;
        assertEquals(expectedHttpResponse, new String(httpResponse.build(), expectedCharset));

    }

    @Test
    public void build_withDateTimeAndWithTextContent_returnsResponseWithContent() {

        final String expectedContent = "Hello, world!";

        final ZonedDateTime dateTime = ZonedDateTime.of(2017, 3, 20, 19, 9, 3, 40, ZoneId.of("GMT"));
        final HttpResponse httpResponse = HttpResponse
                .createWithStatus(HttpStatusCode.OK)
                .dateTime(dateTime)
                .text(expectedContent);

        final Charset expectedCharset = Charset.forName("UTF-8");
        final byte[] expectedContentBytes = expectedContent.getBytes(expectedCharset);
        final String expectedHttpResponse = "HTTP/1.1 200 OK\r\n"
                + "date: Mon, 20 Mar 2017 19:09:03 GMT\r\n"
                + "content-length: " + expectedContentBytes.length + "\r\n"
                + "content-type: text/plain; charset=UTF-8\r\n"
                + "\r\n"
                + expectedContent;
        assertEquals(expectedHttpResponse, new String(httpResponse.build(), expectedCharset));

    }

    @Test
    public void build_withoutDateTime_returnsResponseWithCurrentDateTime() {
        final HttpResponse httpResponse = HttpResponse.createWithStatus(HttpStatusCode.BAD_REQUEST);
        final String response = new String(httpResponse.build(), Charset.forName("UTF-8"));
        assertTrue(response.contains("date: "));
    }

}
