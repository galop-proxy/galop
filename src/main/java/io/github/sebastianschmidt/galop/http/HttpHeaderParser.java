package io.github.sebastianschmidt.galop.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpHeaderParser {

    long calculateTotalLength(InputStream inputStream, int maxHttpHeaderSize) throws IOException;

    long calculateTotalLength(InputStream inputStream, int maxHttpHeaderSize, Runnable startParsingCallback)
            throws IOException;

}
