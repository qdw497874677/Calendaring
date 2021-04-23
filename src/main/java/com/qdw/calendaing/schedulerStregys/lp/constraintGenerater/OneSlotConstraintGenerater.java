package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.requirement.Requirements;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: DefaultConstraintGenerater
 * @Description:
 * @date: 2020/11/16 0016 16:15
 */

// 生成单时隙的约束
@Slf4j
public class OneSlotConstraintGenerater extends AbstractConstraintGenerater {

    private boolean flag = false;


    @Override
    public List<List<Double>> generateOne(NetContext netContext, Collection<Flow> flows, ConstraintType constraintType) {
        if (netContext.getNetwork()==null || netContext.getRequirements()==null || constraintType==null){
            log.error("无效参数");
            return new ArrayList<>();
        }


        List<List<Double>> list = new ArrayList<>();
        switch (constraintType){
            case RONGLIANG:
                list = getRLCons(netContext, flows);
                break;
            case LIULIANG:
                list = getLLCons(netContext, flows);
                break;
            case XUQIU:
                list = getXQCons(netContext, flows);
                break;
        }

        return list;

    }

    // 1<= 2>= 3=
    // 获取链路容量约束
    private List<List<Double>> getRLCons(NetContext netContext, Collection<Flow> flows){
        Requirements requirements = netContext.getRequirements();
        Network network = netContext.getNetwork();
        System.out.println("所有初始流的数量为:"+requirements.getFlowsOfAll());
        ArrayList<List<Double>> res = new ArrayList<>(requirements.getRequirements().size());
        System.out.println("linksdesize:"+network.getLinks().size());
        int timeSlot = flows.iterator().next().getTimeSlot();
        for (Link link : network.getLinks().values()) {
            List<Double> list = new LinkedList<>();
            boolean flag = false;
            for (Flow flow : flows) {
                if (flow.isCover(link)) {
//                    System.out.println("###link:"+link.getId());
                    list.add(1.0);
                    flag = true;
                } else {
                    list.add(0.0);
                }
            }
            if (!flag){
                continue;
            }
            list.add(1.0);
            System.out.println(link.getLinkInfoMap().size());
            list.add(link.getLinkInfo(timeSlot).getResidualCapacity());
            res.add(list);
        }

        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getRLCons size!!!:"+res.size());
        return res;

    }
    // 获取流量约束
    private List<List<Double>> getLLCons(NetContext netContext, Collection<Flow> flows){
        int flowsOfAll = flows.size();
        System.out.println("所有初始流的数量为:"+flowsOfAll);
        List<List<Double>> res = new LinkedList<>();
        int j = 0;
        for (int i = 0; i < flowsOfAll; i++) {
            List<Double> list = new ArrayList<>(flowsOfAll+2);
            for (int k = 0; k < flowsOfAll; k++) {
                if (k==j){
                    list.add(1.0);
                }else {
                    list.add(0.0);
                }
            }
            j++;
//            list.set(i,2);
            list.add(2.0);
            list.add(0.0);
            res.add(list);
        }
        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getLLCons size!!!:"+res.size());
        return res;
    }

    // 获取请求的需求约束
    private List<List<Double>> getXQCons(NetContext netContext, Collection<Flow> flows){
        List<List<Double>> res = new LinkedList<>();
        int flowsOfAll = flows.size();
        System.out.println("所有初始流的数量为:"+flowsOfAll);
        int i = 0;
        Map<Requirements.Requirement,List<Double>> map = new LinkedHashMap<>();
        for (Flow flow : flows) {
            if (!map.containsKey(flow.getThisR())){
                List<Double> list = new LinkedList<>();
                for (int j = 0; j < i; j++) {
                    list.add(0.0);
                }
                list.add(1.0);
                map.put(flow.getThisR(),list);
            }else {
                List<Double> list = map.get(flow.getThisR());
                list.add(1.0);
            }
            i++;
        }
        for (Requirements.Requirement requirement : map.keySet()) {
            List<Double> list = map.get(requirement);
            while (list.size()<flowsOfAll){
                list.add(0.0);
            }
            list.add(3.0);
            list.add((requirement.getDemand()-requirement.getMeetDemand()));
            res.add(list);
        }
        if (res.get(0).size()==0){
            System.out.println();
        }
        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getXQCons size!!!:"+res.size());
        return res;
    }

    // 总流量最大，除了虚拟流
    @Override
    public List<Double> getObjFunc(NetContext netContext, Collection<Flow> flows) {
        List<Double> res = new LinkedList<>();
        for (Flow flow : flows) {
            res.add(getCost(flow, (- flow.getThisR().getPriority()),1000));
    }
        return res;
    }



}
