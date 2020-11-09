package com.thinksee.provider;

import com.thinksee.entity.RpcServiceProperties;

public interface ServiceProvider {
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    Object getService(RpcServiceProperties rpcServiceProperties);

    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    void publishService(Object service);
}
