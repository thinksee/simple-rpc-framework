package com.thinksee.client;

import com.thinksee.api.Hello;
import com.thinksee.api.HelloService;
import com.thinksee.entity.RpcServiceProperties;
import com.thinksee.proxy.RpcClientProxy;
import com.thinksee.remoting.transport.ClientTransport;
import com.thinksee.remoting.transport.socket.SocketRpcClient;

public class RpcFrameworkSimpleClientMain {
    public static void main(String[] args) {
        ClientTransport clientTransport = new SocketRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
