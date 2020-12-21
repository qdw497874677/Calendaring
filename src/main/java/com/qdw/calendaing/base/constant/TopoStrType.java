package com.qdw.calendaing.base.constant;

import java.util.stream.Stream;

public enum TopoStrType {

    YOURONGLIANG("字符串带链路容量",1),
    WURONGLIANG("字符串不带链路容量",2);


    private Integer code;

    TopoStrType(String description, Integer code) {
    }

    public static TopoStrType of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(WURONGLIANG);
    }
}
