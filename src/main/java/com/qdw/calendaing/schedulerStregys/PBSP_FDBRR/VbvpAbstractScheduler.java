package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR;

import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.requirement.Requirements;
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

    // ��ȡһ��ʱ϶��һ��·���������պ���������
    public List<Pair<Path,Double>> getOneStepPath(NetContext netContext, Requirements.Requirement requirement, int timeSlot, double needData){
        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);

        PathProducer pathProducer = netContext.getPathConfig().getPathProducer();
        int numOfNode = netContext.getNetwork().getNodes().size();
        String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, requirement.getMaxBdw());
        List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
        if (paths.isEmpty()){
//            System.out.println("û�к��ʵ�·��");
            return paths;
        }
        Pair<Path,Double> res = paths.get(0);
//        double needDemand = requirement.getDemand()-requirement.getMeetDemand();
        if (res.getValue() > needData){
            res = new Pair<>(res.getKey(),needData);
        }
//        requirement.setMeetDemand(requirement.getMeetDemand() + needDemand);
        return Collections.singletonList(res);
    }

    // ��ȡ���ʱ϶��һ��·���������պ���������
    List<Pair<Path,Double>> getOneStepPaths(NetContext netContext, Requirements.Requirement requirement, int timeSlot, double need){

        int s = requirement.getSNode().getId();
        int d = requirement.getDNode().getId();
        NetTopo topoByTimeSlot = NetTopo.createTopoByTimeSlot(netContext, timeSlot);
        List<Pair<Path,Double>> pairs = new LinkedList<>();
        boolean flag = false;
        double sum = 0;
        PathProducer pathProducer = netContext.getPathConfig().getPathProducer();
        while (true){
            if (flag){
                break;
            }

            int numOfNode = netContext.getNetwork().getNodes().size();
            String pathStr = pathProducer.getPathStr(s, d, numOfNode, topoByTimeSlot, requirement.getMaxBdw());
            List<Pair<Path,Double>> paths = Path.buildPath(netContext.getNetwork(), pathStr);
            if (paths.isEmpty()){
//            System.out.println("û�к��ʵ�·��");
                break;
            }
            Pair<Path, Double> pathDoublePair = paths.get(0);
            Path tempPath = pathDoublePair.getKey();
            double tempSize = pathDoublePair.getValue();
            if (tempSize > need){
                pathDoublePair = new Pair<>(tempPath,need);
                flag = true;
            }
            need -= pathDoublePair.getValue();
//            sum += pathDoublePair.getValue();
            // ��Ȼ����û��ռ�������������˴�������ֱ����Ϊ0����ʾ������
            topoByTimeSlot.updateGraph(tempPath,0);
            pairs.add(pathDoublePair);
        }
        return pairs;
    }

    // ��������������һ��
    void tryProcess(NetContext netContext, List<Requirements.Requirement> requirements){
        for (Requirements.Requirement requirement : requirements) {
            tryProcess(netContext,requirement);
        }
    }

    void tryProcess(NetContext netContext,Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        double need = 0;
        for (int i = l; i <= r; i++) {
            List<Pair<Path, Double>> list = null;
            need = requirement.getDemand() - requirement.getMeetDemand();
            if (netContext.isMulti()){
                list = getOneStepPaths(netContext,requirement, i, need);
            }else {
                list = getOneStepPath(netContext, requirement, i, need);

            }

            for (Pair<Path, Double> oneStepPath : list) {
                if (oneStepPath!=null && oneStepPath.getValue()>0){
                    requirement.addFlow(i, FlowStatus.ZHENGCHANG,oneStepPath.getKey(),oneStepPath.getValue());
//                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//                    need -= oneStepPath.getValue();
                }
            }
        }
    }

}
