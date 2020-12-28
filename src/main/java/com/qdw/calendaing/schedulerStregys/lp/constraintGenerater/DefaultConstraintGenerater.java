package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: DefaultConstraintGenerater
 * @Description:
 * @date: 2020/11/16 0016 16:15
 */
@Slf4j
public class DefaultConstraintGenerater extends AbstractConstraintGenerater {


    @Override
    public List<List<Integer>> generate(NetContext netContext, Collection<Flow> flows, int timeSlot, ConstraintType constraintType) {
        if (netContext.getNetwork()==null || netContext.getRequirements()==null || constraintType==null){
            log.error("��Ч����");
            return new ArrayList<>();
        }

        List<List<Integer>> list = new ArrayList<>();
        switch (constraintType){
            case RONGLIANG:
                list = getRLCons(netContext,flows);
                break;
            case LIULIANG:
                list = getLLCons(netContext,flows);
                break;
            case XUQIU:
                list = getXQCons(netContext,flows);
                break;
        }

        return list;

    }

    // 1< 2> 3=
    // ��ȡ��·����Լ��
    private List<List<Integer>> getRLCons(NetContext netContext,Collection<Flow> flows){
        Requirements requirements = netContext.getRequirements();
        Network network = netContext.getNetwork();
        List<List<Integer>> res = new ArrayList<>(flows.size());
        System.out.println("linksdesize:"+network.getLinks().size());
        for (int i = requirements.getEarliestSlot(); i <= requirements.getLatestSlot(); i++) {
            for (Link link : network.getLinks().values()) {
                List<Integer> list = new LinkedList<>();
                boolean flag = false;
                for (Flow flow : flows) {
                    if (flow.getTimeSlot()==i && flow.isCover(link)) {
                        System.out.println("###link:"+link.getId());
                        list.add(1);
                        flag = true;
                    } else {
                        list.add(0);
                    }
                }
                if (!flag){
                    continue;
                }
                list.add(1);
                list.add((int) link.getLinkInfoMap().get(i).getResidualCapacity());
                res.add(list);
            }
        }

        System.out.println("��������Ϊ��" + res.get(0).size());
        System.out.println("getRLCons size!!!:"+res.size());
        return res;

    }
    // ��ȡ����Լ��
    private List<List<Integer>> getLLCons(NetContext netContext,Collection<Flow> flows){
        int flowsOfAll = flows.size();
        System.out.println("���г�ʼ��������Ϊ:"+flowsOfAll);

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
        System.out.println("��������Ϊ��" + res.get(0).size());
        System.out.println("getLLCons size!!!:"+res.size());
        return res;
    }

    // ��ȡ���������Լ��
    private List<List<Integer>> getXQCons(NetContext netContext,Collection<Flow> flows){
        Requirements requirements = netContext.getRequirements();
        List<List<Integer>> res = new LinkedList<>();
        int flowsOfAll = requirements.getFlowsOfAll();
        System.out.println("���г�ʼ��������Ϊ:"+flowsOfAll);
        int i = 0;
        Map<Requirements.Requirement,List<Integer>> map = new LinkedHashMap<>();
        for (Flow flow : flows) {
            if (!map.containsKey(flow.getThisR())){
                List<Integer> list = new LinkedList<>();
                for (int j = 0; j < i; j++) {
                    list.add(0);
                }
                list.add(1);
                map.put(flow.getThisR(),list);
            }else {
                List<Integer> list = map.get(flow.getThisR());
                list.add(1);
            }
            i++;
        }
        for (Requirements.Requirement requirement : map.keySet()) {
            List<Integer> list = map.get(requirement);
            while (list.size()<flowsOfAll){
                list.add(0);
            }
            list.add(3);
            list.add((int)(requirement.getDemand()-requirement.getMeetDemand()));
            res.add(list);
        }
        System.out.println("��������Ϊ��" + res.get(0).size());
        System.out.println("getXQCons size!!!:"+res.size());
        return res;
    }

    // ��������󣬳���������
    @Override
    public List<Integer> getObjFunc(NetContext netContext,Collection<Flow> flows,int timeSlot) {
        List<Integer> res = new LinkedList<>();
        for (Flow flow : flows) {
            res.add(getCost(flow));
        }

        return res;
    }


}