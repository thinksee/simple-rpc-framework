package com.thinksee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* 解压缩配置类
* */

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for(CompressTypeEnum compressTypeEnum : CompressTypeEnum.values()) {
            if(compressTypeEnum.getCode() == code) {
                return compressTypeEnum.name;
            }
        }
        return null;
    }
}
