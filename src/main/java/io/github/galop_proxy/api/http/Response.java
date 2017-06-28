package io.github.galop_proxy.api.http;

public interface Response extends Message {

    int getStatusCode();

    void setStatusCode(int statusCode);

    // TODO Documentation: Can be empty but not null.
    String getReasonPhrase();

    void setReasonPhrase(String reasonPhrase);

}
