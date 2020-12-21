package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import javafx.util.Pair;

import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpStepsAbstractScheduler
 * @Description:
 * @date: 2020/12/18 0018 16:15
 */
public abstract class VbvpStepsAbstractScheduler implements Scheduler {

    // 计算单个请求的分配结果
    // 为一个时隙的所有请求计算
    boolean process(NetContext netContext,Requirements.Requirement requirement,int timeSlot){
        boolean result = false;
        // 计算路径
        Pair<Path, Double> oneStepPath = getOneStepPath(netContext, requirement, timeSlot);
        // 提高优先级
        requirement.updatePriority(timeSlot);
        if (oneStepPath==null){
            return false;
        }
        double value = oneStepPath.getValue();
        if (requirement.getDemand()-requirement.getMeetDemand()<=value){
            requirement.setAccpted(true);
            value = requirement.getDemand()-requirement.getMeetDemand();
            result = true;
        }
        netContext.getNetwork().updateBandwidth(requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,oneStepPath.getKey(),value));
//        requirement.addDemand(value);
        return result;
    }

    Pair<Path,Double> getOneStepPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);

        PathProducer pathProducer = netContext.getPathProducer();
        int numOfNode = netContext.getNetwork().getNodes().size();
        String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, 1, numOfNode);
        List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
        if (paths.isEmpty()){
//            System.out.println("没有合适的路径");
            return null;
        }
        return paths.get(0);
    }





}
