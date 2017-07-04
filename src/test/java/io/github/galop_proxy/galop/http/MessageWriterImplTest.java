package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.HeaderFields;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link MessageWriterImpl}.
 */
public class MessageWriterImplTest {

    private StartLineWriter startLineWriter;
    private HeaderWriter headerWriter;
    private MessageBodyWriter messageBodyWriter;
    private MessageWriterImpl instance;

    private Request request;
    private Response response;
    private InputStream inputStream;
    private OutputStream outputStream;

    @Before
    public void setUp() {

        startLineWriter = mock(StartLineWriter.class);
        headerWriter = mock(HeaderWriter.class);
        messageBodyWriter = mock(MessageBodyWriter.class);
        instance = new MessageWriterImpl(startLineWriter, headerWriter, messageBodyWriter);

        request = mock(Request.class);
        when(request.getHeaderFields()).thenReturn(new HashMap<>());

        response = mock(Response.class);
        when(response.getHeaderFields()).thenReturn(new HashMap<>());

        inputStream = mock(InputStream.class);
        outputStream = mock(OutputStream.class);

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutStartLineWriter_throwsNullPointerException() {
        new MessageWriterImpl(null, headerWriter, messageBodyWriter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHeaderWriter_throwsNullPointerException() {
        new MessageWriterImpl(startLineWriter, null, messageBodyWriter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMessageBodyWriter_throwsNullPointerException() {
        new MessageWriterImpl(startLineWriter, headerWriter, null);
    }

    // Identity encoding:

    @Test
    public void writeRequest_withContentLengthAndWithoutTransferEncoding_usesIdentityEncodingWithSpecifiedContentLength()
            throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("256"));
        assertIdentityEncodingRequest(256);
    }

    @Test
    public void writeResponse_withContentLengthAndWithoutTransferEncoding_usesIdentityEncodingWithSpecifiedContentLength()
            throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("256"));
        assertIdentityEncodingResponse(256);
    }

    @Test
    public void writeRequest_withIdentityTransferEncoding_usesIdentityEncodingWithSpecifiedContentLength()
            throws IOException{
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("identity"));
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("100"));
        assertIdentityEncodingRequest(100);
    }

    @Test
    public void writeResponse_withIdentityTransferEncoding_usesIdentityEncodingWithSpecifiedContentLength()
            throws IOException{
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("identity"));
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("100"));
        assertIdentityEncodingResponse(100);
    }

    @Test
    public void writeRequest_withoutTransferEncodingAndWithoutContentLength_usesIdentityEncodingWithZeroLength()
            throws IOException {
        assertIdentityEncodingRequest(0);
    }

    @Test
    public void writeResponse_withoutTransferEncodingAndWithoutContentLength_usesIdentityEncodingWithZeroLength()
            throws IOException {
        assertIdentityEncodingResponse(0);
    }

    @Test
    public void writeRequest_withEmptyContentLength_usesIdentityEncodingWithZeroLength() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList(""));
        assertIdentityEncodingRequest(0);
    }

    @Test
    public void writeResponse_withEmptyContentLength_usesIdentityEncodingWithZeroLength() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList(""));
        assertIdentityEncodingResponse(0);
    }

    @Test
    public void writeRequest_withEmptyContentLengthList_usesIdentityEncodingWithZeroLength() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.emptyList());
        assertIdentityEncodingRequest(0);
    }

    @Test
    public void writeResponse_withEmptyContentLengthList_usesIdentityEncodingWithZeroLength() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.emptyList());
        assertIdentityEncodingResponse(0);
    }

    @Test
    public void writeRequest_withEmptyTransferEncoding_usesIdentityEncodingWithZeroLength() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList(""));
        assertIdentityEncodingRequest(0);
    }

    @Test
    public void writeResponse_withEmptyTransferEncoding_usesIdentityEncodingWithZeroLength() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList(""));
        assertIdentityEncodingResponse(0);
    }

    @Test
    public void writeRequest_withEmptyTransferEncodingList_usesIdentityEncodingWithZeroLength() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.emptyList());
        assertIdentityEncodingRequest(0);
    }

    @Test
    public void writeResponse_withEmptyTransferEncodingList_usesIdentityEncodingWithZeroLength() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.emptyList());
        assertIdentityEncodingResponse(0);
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void writeRequest_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("abc"));
        assertFailureRequest();
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void writeResponse_withInvalidContentLength_throwsInvalidHttpHeaderException() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("abc"));
        assertFailureResponse();
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void writeRequest_withNegativeContentLength_throwsInvalidHttpHeaderException() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("-1"));
        assertFailureRequest();
    }

    @Test(expected = InvalidHttpHeaderException.class)
    public void writeResponse_withNegativeContentLength_throwsInvalidHttpHeaderException() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.CONTENT_LENGTH, Collections.singletonList("-1"));
        assertFailureResponse();
    }

    // Chunked encoding:

    @Test
    public void writeRequest_withChunkedTransferEncoding_usesChunkedEncoding() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("chunked"));
        assertChunkedEncodingRequest();
    }

    @Test
    public void writeResponse_withChunkedTransferEncoding_usesChunkedEncoding() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("chunked"));
        assertChunkedEncodingResponse();
    }

    @Test
    public void writeRequest_withChunkedAsLastTransferEncoding_usesChunkedEncoding() throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("gzip, chunked"));
        assertChunkedEncodingRequest();
    }

    @Test
    public void writeResponse_withChunkedAsLastTransferEncoding_usesChunkedEncoding() throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("gzip, chunked"));
        assertChunkedEncodingResponse();
    }

    @Test
    public void writeRequest_withChunkedAsLastTransferEncodingInDifferentHeaderField_usesChunkedEncoding()
            throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Arrays.asList("gzip", "chunked"));
        assertChunkedEncodingRequest();
    }

    @Test
    public void writeResponse_withChunkedAsLastTransferEncodingInDifferentHeaderField_usesChunkedEncoding()
            throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Arrays.asList("gzip", "chunked"));
        assertChunkedEncodingResponse();
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void writeRequest_withUnsupportedTransferEncoding_throwsUnsupportedTransferEncodingException()
            throws IOException {
        request.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("unsupported"));
        assertFailureRequest();
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void writeResponse_withUnsupportedTransferEncoding_throwsUnsupportedTransferEncodingException()
            throws IOException {
        response.getHeaderFields().put(HeaderFields.Response.TRANSFER_ENCODING, Collections.singletonList("unsupported"));
        assertFailureResponse();
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void writeRequest_withoutMessage_throwsNullPointerException() throws IOException {
        instance.writeRequest(null, inputStream, outputStream);
    }

    @Test(expected = NullPointerException.class)
    public void writeRequest_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.writeRequest(request, null, outputStream);
    }

    @Test(expected = NullPointerException.class)
    public void writeRequest_withoutOutputStream_throwsNullPointerException() throws IOException {
        instance.writeRequest(request, inputStream, null);
    }

    @Test(expected = NullPointerException.class)
    public void writeResponse_withoutMessage_throwsNullPointerException() throws IOException {
        instance.writeResponse(null, inputStream, outputStream);
    }

    @Test(expected = NullPointerException.class)
    public void writeResponse_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.writeResponse(response, null, outputStream);
    }

    @Test(expected = NullPointerException.class)
    public void writeResponse_withoutOutputStream_throwsNullPointerException() throws IOException {
        instance.writeResponse(response, inputStream, null);
    }

    // Helper methods:

    private void assertIdentityEncodingRequest(final long length) throws IOException {

        instance.writeRequest(request, inputStream, outputStream);

        verify(startLineWriter).writeRequestLine(same(request), any());
        verify(headerWriter).writeHeader(same(request), any());
        verify(messageBodyWriter).writeIdentityEntity(inputStream, outputStream, length);
        verify(messageBodyWriter, never()).writeChunkedEntity(any(), any());

    }

    private void assertIdentityEncodingResponse(final long length) throws IOException {

        instance.writeResponse(response, inputStream, outputStream);

        verify(startLineWriter).writeStatusLine(same(response), any());
        verify(headerWriter).writeHeader(same(response), any());
        verify(messageBodyWriter).writeIdentityEntity(inputStream, outputStream, length);
        verify(messageBodyWriter, never()).writeChunkedEntity(any(), any());

    }

    private void assertChunkedEncodingRequest() throws IOException {

        instance.writeRequest(request, inputStream, outputStream);

        verify(startLineWriter).writeRequestLine(same(request), any());
        verify(headerWriter).writeHeader(same(request), any());
        verify(messageBodyWriter).writeChunkedEntity(inputStream, outputStream);
        verify(messageBodyWriter, never()).writeIdentityEntity(any(), any(), anyLong());

    }

    private void assertChunkedEncodingResponse() throws IOException {

        instance.writeResponse(response, inputStream, outputStream);

        verify(startLineWriter).writeStatusLine(same(response), any());
        verify(headerWriter).writeHeader(same(response), any());
        verify(messageBodyWriter).writeChunkedEntity(inputStream, outputStream);
        verify(messageBodyWriter, never()).writeIdentityEntity(any(), any(), anyLong());

    }

    private void assertFailureRequest() throws IOException {
        try {
            instance.writeRequest(request, inputStream, outputStream);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertFalse(ex.getMessage().isEmpty());
            throw ex;
        }
    }

    private void assertFailureResponse() throws IOException {
        try {
            instance.writeResponse(response, inputStream, outputStream);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertFalse(ex.getMessage().isEmpty());
            throw ex;
        }
    }

}
