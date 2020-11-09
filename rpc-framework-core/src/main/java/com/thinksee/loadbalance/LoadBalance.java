package com.thinksee.loadbalance;

import com.thinksee.extension.SPI;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddress, String rpcServiceName);
}
