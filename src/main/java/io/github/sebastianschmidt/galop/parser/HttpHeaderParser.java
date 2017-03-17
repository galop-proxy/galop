package io.github.sebastianschmidt.galop.parser;

import java.io.IOException;
import java.io.InputStream;

public interface HttpHeaderParser {

    long calculateTotalLength(InputStream inputStream, int maxHttpHeaderSize) throws IOException;

}