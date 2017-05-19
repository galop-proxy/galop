package io.github.galop_proxy.galop.http;

import java.io.IOException;
import java.io.InputStream;

interface HttpHeaderParser {

    interface Result {

        boolean isChunkedTransferEncoding();

        long getHeaderLength();

        Long getTotalLength();

    }

    Result parse(InputStream inputStream, boolean request) throws IOException;

    Result parse(InputStream inputStream, boolean request, Runnable startParsingCallback) throws IOException;

}
