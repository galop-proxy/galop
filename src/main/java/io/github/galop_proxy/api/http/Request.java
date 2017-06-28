package io.github.galop_proxy.api.http;

public interface Request extends Message {

    String getMethod();

    void setMethod(String method);

    String getRequestTarget();

    void setRequestTarget(String requestTarget);

}
