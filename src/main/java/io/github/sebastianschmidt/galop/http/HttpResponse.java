package io.github.sebastianschmidt.galop.http;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.sebastianschmidt.galop.http.HttpConstants.*;
import static java.util.Objects.requireNonNull;

public final class HttpResponse {

    private static final String TEXT_CONTENT_TYPE = "text/plain";
    private static final Charset DEFAULT_CONTENT_CHARSET = Charset.forName("UTF-8");

    private final HttpStatusCode statusCode;
    private ZonedDateTime dateTime;
    private String contentType;
    private Charset contentCharset;
    private byte[] content;

    public static HttpResponse createWithStatus(final HttpStatusCode statusCode) {
        return new HttpResponse(statusCode);
    }

    private HttpResponse(final HttpStatusCode statusCode) {
        this.statusCode = requireNonNull(statusCode, "statusCode must not be null.");
    }

    public HttpResponse dateTime(final ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public HttpResponse text(final String content) {
        this.contentType = TEXT_CONTENT_TYPE;
        this.contentCharset = DEFAULT_CONTENT_CHARSET;
        this.content = content.getBytes(contentCharset);
        return this;
    }

    public byte[] build() {

        final byte[] header = buildHeader().getBytes(HEADER_CHARSET);
        final byte[] content = buildContent();

        final byte[] headerAndContent = new byte[header.length + content.length];
        System.arraycopy(header, 0, headerAndContent, 0, header.length);
        System.arraycopy(content, 0, headerAndContent, header.length, content.length);
        return headerAndContent;

    }

    private String buildHeader() {
        final StringBuilder builder = new StringBuilder();
        buildStatusLine(builder);
        buildDateHeader(builder);
        buildContentHeaders(builder);
        buildHeaderEnd(builder);
        return builder.toString();
    }

    private void buildStatusLine(final StringBuilder builder) {
        builder.append(HTTP_VERSION);
        builder.append(SPACE);
        builder.append(statusCode.getCode());
        builder.append(SPACE);
        builder.append(statusCode.getReason());
        builder.append(NEW_LINE);
    }

    private void buildDateHeader(final StringBuilder builder) {

        ZonedDateTime dateTime;

        if (this.dateTime != null) {
            dateTime = this.dateTime;
        } else {
            dateTime = ZonedDateTime.now(ZoneId.of("GMT"));
        }

        builder.append(HEADER_DATE_PREFIX);
        builder.append(SPACE);
        builder.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        builder.append(NEW_LINE);

    }

    private void buildContentHeaders(final StringBuilder builder) {

        String type;
        Charset charset;
        byte[] content;

        if (this.content == null) {
            type = TEXT_CONTENT_TYPE;
            charset = DEFAULT_CONTENT_CHARSET;
            content = getDefaultContent().getBytes(DEFAULT_CONTENT_CHARSET);
        } else {
            type = this.contentType;
            charset = this.contentCharset;
            content = this.content;
        }

        builder.append(HEADER_CONTENT_LENGTH_PREFIX);
        builder.append(SPACE);
        builder.append(content.length);
        builder.append(NEW_LINE);
        builder.append(HEADER_CONTENT_TYPE_PREFIX);
        builder.append(SPACE);
        builder.append(type);
        builder.append(VALUE_SEPARATOR);
        builder.append(SPACE);
        builder.append(HEADER_CONTENT_TYPE_CHARSET_PREFIX);
        builder.append(charset);
        builder.append(NEW_LINE);

    }

    private void buildHeaderEnd(final StringBuilder builder) {
        builder.append(NEW_LINE);
    }

    private byte[] buildContent() {

        if (content == null) {
            return getDefaultContent().getBytes(DEFAULT_CONTENT_CHARSET);
        } else {
            return content;
        }

    }

    private String getDefaultContent() {
        return statusCode.getReason();
    }

}
