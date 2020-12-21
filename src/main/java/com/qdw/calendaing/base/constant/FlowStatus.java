package com.qdw.calendaing.base.constant;


import java.util.stream.Stream;

public enum FlowStatus {
    ZHENGCHANG("������",1),
    XUNI("������",2);


    private Integer code;

    FlowStatus(String description, Integer code) {
    }

    public static FlowStatus of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(ZHENGCHANG);
    }

}
