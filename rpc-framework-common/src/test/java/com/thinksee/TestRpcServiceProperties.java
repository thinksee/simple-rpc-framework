package com.thinksee;

import com.thinksee.entity.RpcServiceProperties;

public class TestRpcServiceProperties {
    public static void main(String[] args) {
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties("1", "1", "1");
        rpcServiceProperties.toRpcServiceName();
    }
}
