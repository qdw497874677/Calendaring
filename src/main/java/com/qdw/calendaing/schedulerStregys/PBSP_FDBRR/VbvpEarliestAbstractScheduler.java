package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR;

import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
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
public abstract class VbvpEarliestAbstractScheduler extends VbvpAbstractScheduler {


    // VBVP��ÿ��ʱ϶Ѱ��һ������·����
    public boolean process(NetContext netContext, Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        Map<Integer,List<Pair<Path, Double>>> map = new HashMap<>();
        boolean flag = false;
        double need = requirement.getDemand() - requirement.getMeetDemand();
//        for (int i = l; i <= r; i++) {

        for (;; l++) {
            List<Pair<Path, Double>> oneStepPaths = null;
            if (netContext.isMulti()){
                oneStepPaths = getOneStepPaths(netContext, requirement, l, need);
            }else {
                oneStepPaths = getOneStepPath(netContext, requirement, l, need);
            }

            for (Pair<Path, Double> oneStepPath : oneStepPaths) {

                if (oneStepPath!=null){
                    double value = oneStepPath.getValue();

                    List<Pair<Path, Double>> list = null;
                    if (map.containsKey(l)){
                        list = map.get(l);
                    }else {
                        list = new LinkedList<>();
                        map.put(l,list);
                    }

                    if (value >= need){
                        oneStepPath = new Pair<>(oneStepPath.getKey(), need);
                        flag = true;
//                        break;
                    }
                    need -= oneStepPath.getValue();
                    list.add(oneStepPath);
                }
            }
            if (flag){
                break;
            }

        }
        // �ܹ���ɣ����Ԥ������
        requirement.setAccpted(true);
        requirement.setRealFinishSlot(l);
        System.out.println();
        map.keySet().forEach(timeSlot->{
            for (Pair<Path, Double> pathDoublePair : map.get(timeSlot)) {
                requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),pathDoublePair.getValue());
            }

        });


        return flag;


//        if (flag){
//            // �ܹ���ɣ����Ԥ������
//            requirement.setAccpted(true);
//            requirement.setRealFinishSlot(l);
//            map.keySet().forEach(timeSlot->{
//                for (Pair<Path, Double> pathDoublePair : map.get(timeSlot)) {
//                    requirement.addFlow(timeSlot, FlowStatus.ZHENGCHANG,pathDoublePair.getKey(),pathDoublePair.getValue());
//                }
//
//            });
//
//            return true;
//        }
//        return false;
    }

    // VBVP��ÿ��ʱ϶Ѱ�Ҷ�������·��
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
//                // �������˴���Ϊ�˼����·��
//                assert cloneTopo != null;
//                cloneTopo.updateGraphToDe(oneStepPath.getKey(),oneStepPath.getValue());
//            }
//        }
//        if (flag){
//            // �ܹ���ɣ����Ԥ������
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
//            System.out.println("û�к��ʵ�·��");
//            return null;
//        }
//        return paths.get(0);
//    }
//    public Pair<Path,Double> getOneSlotPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot){
//        return getOneSlotPath(netContext,NetTopo.createTopoByTimeSlot(netContext, timeSlot), requirement);
//    }




}
