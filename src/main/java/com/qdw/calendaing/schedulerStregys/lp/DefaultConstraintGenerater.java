package com.qdw.calendaing.schedulerStregys.lp;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: DefaultConstraintGenerater
 * @Description:
 * @date: 2020/11/16 0016 16:15
 */
@Slf4j
public class DefaultConstraintGenerater implements ConstraintGenerater {

    private boolean flag = false;

    @Override
    public List<List<Integer>> generate(NetContext netContext, ConstraintType constraintType) {
        if (netContext.getNetwork()==null || netContext.getRequirements()==null || constraintType==null){
            log.error("无效参数");
            return new ArrayList<>();
        }
        if (!flag){
            netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork());
            flag = true;
        }

        List<List<Integer>> list = new ArrayList<>();
        switch (constraintType){
            case RONGLIANG:
                list = getRLCons(netContext);
                break;
            case LIULIANG:
                list = getLLCons(netContext);
                break;
            case XUQIU:
                list = getXQCons(netContext);
                break;
        }

        return list;

    }

    // 1< 2> 3=
    // 获取链路容量约束
    private List<List<Integer>> getRLCons(NetContext netContext){
        Requirements requirements = netContext.getRequirements();
        Network network = netContext.getNetwork();
        System.out.println("所有初始流的数量为:"+requirements.getFlowsOfAll());
        ArrayList<List<Integer>> res = new ArrayList<>(requirements.getRequirements().size());
        System.out.println("linksdesize:"+network.getLinks().size());
        for (int i = netContext.getRequirements().getEarliestSlot(); i <= netContext.getRequirements().getLatestSlot(); i++) {
            for (Link link : network.getLinks().values()) {
                List<Integer> list = new LinkedList<>();
                for (Requirements.Requirement requirement : requirements.getRequirements()) {
                    Map<Integer, List<Flow>> flowsOfR = requirement.getFlowsOfR();
                    for (List<Flow> flows : flowsOfR.values()) {
                        for (Flow flow : flows) {
                            if (flow.isCover(link) && flow.getTimeSlot() == i) {
                                System.out.println("###link:"+link.getId());
                                list.add(1);
                            } else {
                                list.add(0);
                            }

                        }
                    }
                }
                list.add(1);
                list.add((int) link.getLinkInfoMap().get(i).getCapacity());
                res.add(list);
            }
        }

        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getRLCons size!!!:"+res.size());
        return res;

    }
    // 获取流量约束
    private List<List<Integer>> getLLCons(NetContext netContext){
        Requirements requirements = netContext.getRequirements();

        int flowsOfAll = requirements.getFlowsOfAll();
        System.out.println("所有初始流的数量为:"+flowsOfAll);

        List<List<Integer>> res = new LinkedList<>();
        int j = 0;
        for (int i = 0; i < flowsOfAll; i++) {

            List<Integer> list = new ArrayList<>(flowsOfAll+2);
            for (int k = 0; k < flowsOfAll; k++) {
                if (k==j){
                    list.add(1);
                }else {
                    list.add(0);
                }
            }
            j++;
//            list.set(i,2);
            list.add(2);
            list.add(0);
            res.add(list);
        }
        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getLLCons size!!!:"+res.size());
        return res;
    }

    // 获取请求的需求约束
    private List<List<Integer>> getXQCons(NetContext netContext){
        Requirements requirements = netContext.getRequirements();
        List<List<Integer>> res = new LinkedList<>();
        int flowsOfAll = requirements.getFlowsOfAll();
        System.out.println("所有初始流的数量为:"+flowsOfAll);
        int i = 0;
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            List<Integer> list = new LinkedList<>();
            Map<Integer, List<Flow>> flowsOfR = requirement.getFlowsOfR();
            for (int j = 0; j < i; j++) {
                list.add(0);
            }
            for (List<Flow> flows : flowsOfR.values()) {
                for (int j = 0; j < flows.size(); j++) {
                    i++;
                    list.add(1);
                }
            }
            while (list.size() < flowsOfAll){
                list.add(0);
            }
            list.add(3);
            list.add((int)requirement.getDemand());
            res.add(list);
        }
        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getXQCons size!!!:"+res.size());
        return res;
    }

    // 总流量最大，除了虚拟流
    @Override
    public List<Integer> getObjFunc(NetContext netContext) {
        Requirements requirements = netContext.getRequirements();
        List<Integer> res = new LinkedList<>();
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    res.add(getCost(flow));
                }
            }
        }

        return res;
    }



    private Integer getCost(Flow flow){

        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
            return flow.getPath().getPath().size();
        }else {
            return 1000;
        }
    }
}
