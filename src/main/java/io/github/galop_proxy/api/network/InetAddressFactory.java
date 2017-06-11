package io.github.galop_proxy.api.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface InetAddressFactory {

    InetAddress createByName(String host) throws UnknownHostException;

}
