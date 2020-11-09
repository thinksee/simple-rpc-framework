package com.thinksee.remoting.transport.netty.server;

import com.thinksee.enums.CompressTypeEnum;
import com.thinksee.enums.RpcResponseCodeEnum;
import com.thinksee.enums.SerializationTypeEnum;
import com.thinksee.factory.SingletonFactory;
import com.thinksee.remoting.constants.RpcConstants;
import com.thinksee.remoting.dto.RpcMessage;
import com.thinksee.remoting.dto.RpcRequest;
import com.thinksee.remoting.dto.RpcResponse;
import com.thinksee.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    RpcMessage rpcMessage = new RpcMessage();
                    rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                    rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        RpcMessage rpcMessage = new RpcMessage();
                        rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                        rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                        rpcMessage.setData(rpcResponse);
                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        RpcMessage rpcMessage = new RpcMessage();
                        rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                        rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                        rpcMessage.setData(rpcResponse);
                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        log.error("not writable now, message dropped");
                    }
                }

            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
