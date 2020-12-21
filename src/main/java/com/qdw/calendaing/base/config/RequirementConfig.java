package com.qdw.calendaing.base.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @PackageName:com.qdw.calendaing.base.config
 * @ClassName: RequirementConfig
 * @Description:
 * @date: 2020/11/9 0009 18:58
 */
@Data
@AllArgsConstructor
public class RequirementConfig {
    private int numOfR;
    // ���翪ʼʱ϶
    private int earliestSlot;
    // �������ʱ϶
    private int latestSlot;
    // ����������
    private int demandBase;
}
