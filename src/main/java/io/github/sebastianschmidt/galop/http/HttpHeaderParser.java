package io.github.sebastianschmidt.galop.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpHeaderParser {

    interface Result {

        boolean isChunkedTransferEncoding();

        long getHeaderLength();

        Long getTotalLength();

    }

    Result parse(InputStream inputStream, int maxHttpHeaderSize) throws IOException;

    Result parse(InputStream inputStream, int maxHttpHeaderSize, Runnable startParsingCallback)
            throws IOException;

}
