package com.qdw.calendaing.base.config;

import com.alibaba.fastjson.JSONObject;
import com.qdw.calendaing.base.requirementBase.FixByJsonProducer;
import com.qdw.calendaing.base.requirementBase.RequirementProducer;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_PM;
import com.qdw.calendaing.base.requirementBase.priority.PriorityModifier;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

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
    // 请求生成器
    private RequirementProducer requirementProducer;
    // 请求优先级修改器
    private PriorityModifier priorityModifier;

    // 预设固定请求
    private List<JSONObject> reqsJson;

    public RequirementConfig(int numOfR,int earliestSlot,int latestSlot,int demandBase,RequirementProducer requirementProducer){
        this.numOfR = numOfR;
        this.earliestSlot = earliestSlot;
        this.latestSlot = latestSlot;
        this.demandBase = demandBase;
        this.requirementProducer = requirementProducer;
        this.priorityModifier = new MaxCS_PM();
    }

    public RequirementConfig(int numOfR,int earliestSlot,int latestSlot,int demandBase,RequirementProducer requirementProducer,PriorityModifier priorityModifier){
        this.numOfR = numOfR;
        this.earliestSlot = earliestSlot;
        this.latestSlot = latestSlot;
        this.demandBase = demandBase;
        this.requirementProducer = requirementProducer;
        this.priorityModifier = priorityModifier;
    }

    public RequirementConfig(int earliestSlot,int latestSlot,PriorityModifier priorityModifier,List<JSONObject> reqsJson){
        this.earliestSlot = earliestSlot;
        this.latestSlot = latestSlot;
        this.numOfR = reqsJson.size();
        this.priorityModifier = priorityModifier;
        this.reqsJson = reqsJson;
        this.requirementProducer = new FixByJsonProducer();

    }
}
