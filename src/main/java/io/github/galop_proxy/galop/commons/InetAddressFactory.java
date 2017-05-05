package io.github.galop_proxy.galop.commons;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface InetAddressFactory {

    InetAddress createByName(String host) throws UnknownHostException;

}
