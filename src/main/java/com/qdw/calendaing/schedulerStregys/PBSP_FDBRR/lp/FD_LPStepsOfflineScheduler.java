package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.schedulerStregys.lp.AbstractLPScheduler;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

// TODO
@Data
@AllArgsConstructor
public class FD_LPStepsOfflineScheduler extends AbstractLPScheduler {

    {
        setConstraintGenerater(new OneSlotConstraintGenerater());
    }

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        // ��ʼ���������
        netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork(),false);

        Requirements requirements = netContext.getRequirements();
        int l = requirements.getEarliestSlot();
        int r = requirements.getLatestSlot();
        // ���ݿ�ʼʱ϶����Ϊ�˷�����ȡ����
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        List<Requirements.Requirement> curList = new ArrayList<>();
        CalendaingResult calendaingResult = new CalendaingResult();
        long start = System.currentTimeMillis();
        for (;; l++) {
            System.out.println("��ʱʱ϶Ϊ��"+l);
            List<Flow> flows = new LinkedList<>();

            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==l){
                Requirements.Requirement poll = unprocessed.poll();
                curList.add(poll);
            }
            for (Requirements.Requirement requirement : curList) {
                flows.addAll(requirement.getFlowsOfR(l));
                Flow xunniFlow = Flow.getXUNNIFlow(l, requirement);
                requirement.getFlowsOfR(l).add(xunniFlow);
                flows.add(xunniFlow);
            }
//            for (Flow flow : flows) {
//                System.out.println(flow);
//            }
            updateP(flows,l,netContext);
//            for (Requirements.Requirement requirement : requirements.getRequirements()) {
//                // �����û����ȫ�����㣬���е�ǰʱ϶��ʼ����
//                if (!requirement.isAccpted() && requirement.getFlowsOfR().containsKey(l)){
//                    flows.addAll(requirement.getFlowsOfR().get(l));
//                    Flow xunniFlow = Flow.getXUNNIFlow(l, requirement);
//                    requirement.getFlowsOfR().get(l).add(xunniFlow);
//                    flows.add(xunniFlow);
//                }
//            }
            if (flows.isEmpty()){
                continue;
            }
            List<Constraint> constraints = getConstraints(netContext,flows);

//            for (Constraint constraint : constraints) {
//                System.out.println(constraint);
//            }

            String objectiveFunction = constraintGenerater.getObjFunc(netContext,flows)
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "))
                    ;
            System.out.println(objectiveFunction);
            int flowsOfAll = flows.size();
            System.out.println("flowsOfAll:"+flowsOfAll);


            double[] res = LpUtil.solveLp(constraints, objectiveFunction, flowsOfAll,2);
            System.out.println(Arrays.toString(res));
            setFlows(res,flows,l);

//            curList.removeIf(Requirements.Requirement::isAccpted);
            Iterator<Requirements.Requirement> iterator = curList.iterator();
            while (iterator.hasNext()){
                Requirements.Requirement next = iterator.next();
                if (next.isAccpted()){
                    calendaingResult.accept(next);
                    netContext.getNetwork().updateBandwidth(next.getFlowsOfR());
                    iterator.remove();
                }
            }
            if (curList.isEmpty() && unprocessed.isEmpty()){
                break;
            }
        }
        long time  = System.currentTimeMillis() - start;

        calendaingResult.setTotalTime(time);

//        calendaingResult.setResultOneTime(netContext.getRequirements());
//        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
//            netContext.getNetwork().updateBandwidth(requirement.getFlowsOfR());
//        }

        return calendaingResult;
    }


    @Override
    public String toString() {
        return "LPStepsOfflineScheduler{" +
                "���=" + "���ߡ���ʱ϶��LP" +
                ", constraintGenerater=" + constraintGenerater.getClass().getSimpleName() +
                '}';
    }
}
