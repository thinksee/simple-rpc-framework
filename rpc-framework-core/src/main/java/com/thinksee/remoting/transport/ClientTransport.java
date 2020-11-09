package com.thinksee.remoting.transport;

import com.thinksee.extension.SPI;
import com.thinksee.remoting.dto.RpcRequest;

@SPI
public interface ClientTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
