package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.pathBase.PathProducer;
import javafx.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpStepsAbstractScheduler
 * @Description:
 * @date: 2020/12/18 0018 16:15
 */
public abstract class VbvpStepsAbstractScheduler extends VbvpAbstractScheduler {

    // 计算单个请求的分配结果
    // 为一个时隙的所有请求计算
    boolean process(NetContext netContext,Requirements.Requirement requirement,int timeSlot){
        boolean result = false;
        // 计算路径
        List<Pair<Path, Double>> oneStepPath = null;

        // 是否支持多路径
        if (netContext.isMulti()){
            oneStepPath = getOneStepPaths(netContext,requirement,timeSlot);
        }else {
            oneStepPath = getOneStepPath(netContext, requirement, timeSlot);
        }

        // 提高优先级

        if (oneStepPath==null || oneStepPath.size()==0){
            return false;
        }
        for (Pair<Path, Double> pathDoublePair : oneStepPath) {
            double value = pathDoublePair.getValue();
            if (requirement.getDemand()-requirement.getMeetDemand()<=value){
                requirement.setAccpted(true);
                value = requirement.getDemand()-requirement.getMeetDemand();
                result = true;
            }
            netContext.getNetwork().updateBandwidth(requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),value));
        }
        requirement.updatePriority(timeSlot,netContext.getRequirementConfig().getPriorityModifier());
//        requirement.addDemand(value);
        return result;
    }







}
