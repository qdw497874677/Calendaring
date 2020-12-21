package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpEarliestAbstractScheduler
 * @Description:
 * @date: 2020/12/18 0018 11:18
 */
public abstract class  VbvpEarliestAbstractScheduler implements Scheduler {


    boolean process(NetContext netContext, Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        double sum = 0;
        Map<Integer,Pair<Path,Double>> map = new HashMap<>();
        boolean flag = false;
        for (int i = l; i <= r; i++) {
            Pair<Path,Double> oneStepPath = getOneSlotPath(netContext, requirement, i);
            if (oneStepPath!=null){
                double value = oneStepPath.getValue();
                sum += value;
                map.put(i,oneStepPath);
                if (sum>=requirement.getDemand()){
                    map.put(i,new Pair<>(oneStepPath.getKey(),requirement.getDemand()-(sum-value)));
                    flag = true;
                    break;
                }
            }
        }
        if (flag){
            // 能够完成，添加预留的流
            requirement.setAccpted(true);
            map.keySet().forEach(timeSlot->{
                requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,map.get(timeSlot).getKey(),map.get(timeSlot).getValue());
            });
            return true;
        }
        return false;
    }

    public Pair<Path,Double> getOneSlotPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);

        PathProducer pathProducer = netContext.getPathProducer();
        int numOfNode = netContext.getNetwork().getNodes().size();
        String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, 1, numOfNode);
        List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
        if (paths.isEmpty()){
            System.out.println("没有合适的路径");
            return null;
        }
        return paths.get(0);
    }



}
