package com.thinksee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 当interface有多个实现类的时候，按照分组进行划分
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RpcServiceProperties {
    private String version;
    private String group;
    private String serviceName;

    public String toRpcServiceName() {
        return getServiceName() + getGroup() + getVersion();
    }
}
