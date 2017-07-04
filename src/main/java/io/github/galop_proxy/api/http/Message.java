package io.github.galop_proxy.api.http;

import java.util.List;
import java.util.Map;

public interface Message {

    Version getVersion();

    void setVersion(Version version);

    Map<String, List<String>> getHeaderFields();

}
