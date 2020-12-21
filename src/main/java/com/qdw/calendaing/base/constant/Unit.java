package com.qdw.calendaing.base.constant;

import java.util.stream.Stream;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: Unit
 * @Description:
 * @date: 2020/11/8 0008 18:48
 */
public enum  Unit {

    HOURS("Ð¡Ê±",1),
    MINUTES("·ÖÖÓ",2);


    private Integer code;

    Unit(String description, Integer code) {
    }

    public static Unit of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(MINUTES);
    }
}
