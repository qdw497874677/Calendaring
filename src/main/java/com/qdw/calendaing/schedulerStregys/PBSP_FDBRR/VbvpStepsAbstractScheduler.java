package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR;

import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.requirement.Requirements;
import javafx.util.Pair;

import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpStepsAbstractScheduler
 * @Description:
 * @date: 2020/12/18 0018 16:15
 */
public abstract class VbvpStepsAbstractScheduler extends VbvpAbstractScheduler {

    // 计算单个请求的分配结果，并且更新链路带宽
    // 为一个时隙的所有请求计算
    boolean process(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
        boolean result = false;
        // 计算路径
        List<Pair<Path, Double>> oneStepPath = null;
        double need = requirement.getDemand() - requirement.getMeetDemand();
        // 是否支持多路径
        if (netContext.isMulti()){
            oneStepPath = getOneStepPaths(netContext, requirement, timeSlot, need);
        }else {
            oneStepPath = getOneStepPath(netContext, requirement, timeSlot, need);
        }


        if (oneStepPath==null || oneStepPath.size()==0){
            return false;
        }
        for (Pair<Path, Double> pathDoublePair : oneStepPath) {
            double value = pathDoublePair.getValue();
            if (need <= value){
                requirement.setAccpted(true);
                value = need;
                result = true;
            }
            need -= value;
            netContext.getNetwork().updateBandwidth(requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),value));
            if (requirement.isAccpted()){
                break;
            }
        }
//        requirement.updatePriority(timeSlot,netContext.getRequirementConfig().getPriorityModifier());
//        requirement.addDemand(value);
        return result;
    }







}
