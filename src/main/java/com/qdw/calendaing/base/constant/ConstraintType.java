package com.qdw.calendaing.base.constant;

import java.util.stream.Stream;

public enum ConstraintType {

    RONGLIANG("��·����Լ��",1),
    XUQIU("��������Լ��",2),
    LIULIANG("�����Ǹ�Լ��",3),
    MAXBDW("������Լ��",4);


    private Integer code;

    ConstraintType(String description, Integer code) {
    }

    public static ConstraintType of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(RONGLIANG);
    }
}
