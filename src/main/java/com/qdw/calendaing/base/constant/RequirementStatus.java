package com.qdw.calendaing.base.constant;


import java.util.stream.Stream;

public enum RequirementStatus {
    NOTSTARTED("未开始调度",1),
    PROCESS("调度过程中",2),
    INCOMPLETE("调度未完成",2),
    COMPLETE("调度完成",3),
    REFUSE("请求被拒绝",4);


    private Integer code;

    RequirementStatus(String description, Integer code) {
    }

    public static RequirementStatus of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(REFUSE);
    }

}
