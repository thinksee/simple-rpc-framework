package com.thinksee.server;

import com.thinksee.api.HelloService;
import com.thinksee.entity.RpcServiceProperties;
import com.thinksee.remoting.transport.socket.SocketRpcServer;
import com.thinksee.server.impl.HelloServiceImpl;

public class RpcFrameworkSimpleServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        socketRpcServer.registerService(helloService, rpcServiceProperties);
        socketRpcServer.start();
    }
}
