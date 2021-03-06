package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.pathBase.PathProducer;
import com.qdw.calendaing.schedulerStregys.Scheduler;
import javafx.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/18 0018 21:12
 */
public abstract class VbvpAbstractScheduler implements Scheduler {

    // 获取一个时隙的一条路径，尽量刚好满足需求
    List<Pair<Path,Double>> getOneStepPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);

        PathProducer pathProducer = netContext.getPathConfig().getPathProducer();
        int numOfNode = netContext.getNetwork().getNodes().size();
        String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, requirement.getMaxBdw());
        List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
        if (paths.isEmpty()){
//            System.out.println("没有合适的路径");
            return paths;
        }
        Pair<Path,Double> res = paths.get(0);
        double needDemand = requirement.getDemand()-requirement.getMeetDemand();
        if (res.getValue() > needDemand){
            res = new Pair<>(res.getKey(),needDemand);
        }
        return Collections.singletonList(res);
    }

    // 获取多个时隙的一条路径，可能带宽超过需求
    List<Pair<Path,Double>> getOneStepPaths(NetContext netContext, Requirements.Requirement requirement, int timeSlot){

        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);
        List<Pair<Path,Double>> pairs = new LinkedList<>();
        boolean flag = false;
        double sum = 0;
        double need = requirement.getDemand()-requirement.getMeetDemand();
        while (true){
            if (flag){
                break;
            }
            PathProducer pathProducer = netContext.getPathConfig().getPathProducer();
            int numOfNode = netContext.getNetwork().getNodes().size();
            String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, requirement.getMaxBdw());
            List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
            if (paths.isEmpty()){
//            System.out.println("没有合适的路径");
                break;
            }
            for (Pair<Path, Double> path : paths) {
                if (flag){
                    break;
                }
                if (path.getValue()+sum>need){
                    path = new Pair<>(path.getKey(),need-sum);
                    flag = true;
                }
                sum += path.getValue();
                pairs.add(path);
//                topoByTimeSlot.updateGraphToDe(path.getKey(),path.getValue());
                // 虽然可以没有占满，但是限制了带宽，所以直接置为0，表示不可用
                topoByTimeSlot.updateGraph(path.getKey(),0);
            }
        }
        return pairs;
    }

    void tryProcess(NetContext netContext, List<Requirements.Requirement> requirements){
        for (Requirements.Requirement requirement : requirements) {
            tryProcess(netContext,requirement);
        }
    }

    void tryProcess(NetContext netContext,Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        for (int i = l; i <= r; i++) {
            List<Pair<Path, Double>> list = null;
            if (netContext.isMulti()){
                list = getOneStepPaths(netContext,requirement,i);
            }else {
                list = getOneStepPath(netContext, requirement, i);

            }
            for (Pair<Path, Double> oneStepPath : list) {
                if (oneStepPath!=null && oneStepPath.getValue()>0){
                    requirement.addFlow(i, FlowStatus.ZHENGCHANG,oneStepPath.getKey(),oneStepPath.getValue());
//                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                }
            }
        }
    }

}
