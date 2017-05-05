package io.github.galop_proxy.galop.http;

import java.io.IOException;

public class UnsupportedTransferEncodingException extends IOException {

    /**
     *
     * @param transferEncoding The name of the unsupported Transfer-Encoding.
     */
    public UnsupportedTransferEncodingException(final String transferEncoding) {
        super("The Transfer-Encoding " + transferEncoding + " is not supported.");
    }

}
