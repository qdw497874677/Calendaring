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
    // 最早开始时隙
    private int earliestSlot;
    // 最晚结束时隙
    private int latestSlot;
    // 传输量基数
    private int demandBase;
}
