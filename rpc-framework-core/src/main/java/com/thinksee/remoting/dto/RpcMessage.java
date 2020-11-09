package com.thinksee.remoting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcMessage {
    // rpc message type
    private byte messageType;
    // serialization type
    private byte codec;
    // compress type
    private byte compress;
    // request id
    private int requestId;
    // request data
    private Object data;
}
