package com.thinksee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KYRO((byte) 0x01, "kyro");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for(SerializationTypeEnum serializationTypeEnum : SerializationTypeEnum.values()) {
            if(serializationTypeEnum.code == code) {
                return serializationTypeEnum.name;
            }
        }
        return null;
    }
}
