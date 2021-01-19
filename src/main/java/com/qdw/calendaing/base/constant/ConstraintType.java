package com.qdw.calendaing.base.constant;

import java.util.stream.Stream;

public enum ConstraintType {

    RONGLIANG("链路容量约束",1),
    XUQIU("请求需求约束",2),
    LIULIANG("流量非负约束",3),
    MAXBDW("最大带宽约束",4);


    private Integer code;

    ConstraintType(String description, Integer code) {
    }

    public static ConstraintType of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(RONGLIANG);
    }
}
