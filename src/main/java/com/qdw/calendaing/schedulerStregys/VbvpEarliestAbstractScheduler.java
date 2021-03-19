package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.requirement.Requirements;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpEarliestAbstractScheduler
 * @Description:
 * @date: 2020/12/18 0018 11:18
 */
public abstract class  VbvpEarliestAbstractScheduler extends VbvpAbstractScheduler {


    // VBVP，每个时隙寻找一条可用路径，
    boolean process(NetContext netContext, Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        double sum = 0;
        Map<Integer,List<Pair<Path, Double>>> map = new HashMap<>();
        boolean flag = false;
        for (int i = l; i <= r; i++) {
            List<Pair<Path, Double>> oneStepPaths = null;
            if (netContext.isMulti()){
                oneStepPaths = getOneStepPaths(netContext,requirement,i);
            }else {
                oneStepPaths = getOneStepPath(netContext, requirement, i);
            }

            for (Pair<Path, Double> oneStepPath : oneStepPaths) {

                if (flag){
                    break;
                }
                if (oneStepPath!=null){
                    double value = oneStepPath.getValue();
                    sum += value;

                    List<Pair<Path, Double>> list = null;
                    if (map.containsKey(i)){
                        list = map.get(i);
                    }else {
                        list = new LinkedList<>();
                        map.put(i,list);
                    }

                    if (sum>=requirement.getDemand()){
                        list.add(new Pair<>(oneStepPath.getKey(),requirement.getDemand()-(sum-value)));
                        flag = true;
                        break;
                    }else {
                        list.add(oneStepPath);
                    }
                }
            }

        }
        if (flag){
            // 能够完成，添加预留的流
            requirement.setAccpted(true);
            map.keySet().forEach(timeSlot->{
                for (Pair<Path, Double> pathDoublePair : map.get(timeSlot)) {
                    requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),pathDoublePair.getValue());
                }

            });
            return true;
        }
        return false;
    }

    // VBVP，每个时隙寻找多条可用路径
//    boolean processByMultipath(NetContext netContext, Requirements.Requirement requirement){
//        NetTopo cloneTopo = null;
//
////        NetTopo originalNetTopo = netContext.getNetwork().getNetTopo();
////        netContext.getNetwork().setNetTopo(cloneTopo);
//
//        int l = requirement.getReadySlot();
//        int r = requirement.getDeadline();
//        double sum = 0;
//        Map<Integer,List<Pair<Path,Double>>> map = new HashMap<>();
//        boolean flag = false;
//        for (int i = l; i <= r; i++) {
//            if (flag){
//                break;
//            }
//            cloneTopo = NetTopo.createTopoByTimeSlot(netContext,i);
//            while (true){
//                Pair<Path,Double> oneStepPath = getOneSlotPath(netContext,cloneTopo, requirement);
//                if (oneStepPath==null || oneStepPath.getValue()<=0){
//                    break;
//                }
//                double value = oneStepPath.getValue();
//                sum += value;
//                List<Pair<Path, Double>> pairs = map.computeIfAbsent(i, k -> new LinkedList<>());
//
//                if (sum>=requirement.getDemand()){
//                    pairs.add(new Pair<>(oneStepPath.getKey(),requirement.getDemand()-(sum-value)));
//                    System.out.println("sum:"+sum+" demand:"+requirement.getDemand());
//                    flag = true;
//                    break;
//                }
//                pairs.add(oneStepPath);
//                // 更新拓扑带宽，为了计算多路径
//                assert cloneTopo != null;
//                cloneTopo.updateGraphToDe(oneStepPath.getKey(),oneStepPath.getValue());
//            }
//        }
//        if (flag){
//            // 能够完成，添加预留的流
//            requirement.setAccpted(true);
//            map.keySet().forEach(timeSlot->{
//                if (map.containsKey(timeSlot)){
//                    for (Pair<Path, Double> pathDoublePair : map.get(timeSlot)) {
//                        requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),pathDoublePair.getValue());
////                        System.out.println("@@@@@@ "+ timeSlot +":"+pathDoublePair.getKey().getPathStr()+" "+pathDoublePair.getValue());
//                    }
//                }
//            });
//
//            return true;
//        }
//        return false;
//    }

//    public Pair<Path,Double> getOneSlotPath(NetContext netContext,NetTopo netTopo, Requirements.Requirement requirement){
//        int s = requirement.getSNode().getId();
//        int d = requirement.getDNode().getId();
////        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);
//
//        PathProducer pathProducer = netContext.getPathConfig().getPathProducer();
//        int numOfNode = netContext.getNetwork().getNodes().size();
//        String pathStr = pathProducer.getPathStr(s, d, numOfNode, netTopo, requirement.getMaxBdw());
//        List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
//        if (paths.isEmpty()){
//            System.out.println("没有合适的路径");
//            return null;
//        }
//        return paths.get(0);
//    }
//    public Pair<Path,Double> getOneSlotPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
//        return getOneSlotPath(netContext,NetTopo.createTopoByTimeSlot(netContext, timeSlot), requirement);
//    }




}
