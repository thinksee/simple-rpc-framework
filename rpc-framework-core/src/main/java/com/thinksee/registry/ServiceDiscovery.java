package com.thinksee.registry;

import com.thinksee.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String rpcServiceName);
}
