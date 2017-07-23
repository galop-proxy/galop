package io.github.galop_proxy.galop.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

interface HeaderParser {

    Map<String, List<String>> parseRequestHeaders(LineReader lineReader) throws IOException;

    Map<String, List<String>> parseResponseHeaders(LineReader lineReader) throws IOException;

}
