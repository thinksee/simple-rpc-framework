package com.thinksee.client;

import com.thinksee.annotation.RpcScan;
import com.thinksee.remoting.transport.netty.client.NettyClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.thinksee"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) annotationConfigApplicationContext.getBean("helloController");
        helloController.test();
    }
}
