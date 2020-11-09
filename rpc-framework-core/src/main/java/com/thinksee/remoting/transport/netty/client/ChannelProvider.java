package com.thinksee.remoting.transport.netty.client;

import com.thinksee.factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;
    private final NettyClient nettyClient;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
        nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if(channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if(null != channel && channel.isActive()) {
                return channel;
            }else {
                channelMap.remove(key);
            }
        }

        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }
}
