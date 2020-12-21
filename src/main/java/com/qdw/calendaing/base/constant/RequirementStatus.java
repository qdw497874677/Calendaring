package com.qdw.calendaing.base.constant;


import java.util.stream.Stream;

public enum RequirementStatus {
    NOTSTARTED("δ��ʼ����",1),
    PROCESS("���ȹ�����",2),
    INCOMPLETE("����δ���",2),
    COMPLETE("�������",3),
    REFUSE("���󱻾ܾ�",4);


    private Integer code;

    RequirementStatus(String description, Integer code) {
    }

    public static RequirementStatus of(Integer code){
        return Stream.of(values()).filter(a -> a.code.equals(code)).findAny().orElse(REFUSE);
    }

}
