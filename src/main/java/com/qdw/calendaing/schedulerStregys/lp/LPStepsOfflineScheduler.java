package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_PM;
import com.qdw.calendaing.base.requirementBase.priority.PriorityModifier;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.ConstraintGenerater;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.OneSlotConstraintGenerater;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

// TODO
@Data
@AllArgsConstructor
public class LPStepsOfflineScheduler extends AbstractLPScheduler {

    {
        setConstraintGenerater(new OneSlotConstraintGenerater());
    }

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        // 初始化请求的流
        netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork(),false);

        Requirements requirements = netContext.getRequirements();
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        int l = requirements.getEarliestSlot();
        int r = requirements.getLatestSlot();

        // 设置优先级更新器
        PriorityModifier priorityModifier = new MaxCS_PM();
        List<Requirements.Requirement> processedR = new LinkedList<>();
        CalendaingResult calendaingResult = new CalendaingResult();
        long start = System.currentTimeMillis();
        for (int i = l; i <= r; i++) {
            List<Flow> flows = new LinkedList<>();
            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==i){
                processedR.add(unprocessed.poll());
            }
            for (Requirements.Requirement requirement : processedR) {
                flows.addAll(requirement.getFlowsOfR().get(i));
                Flow xunniFlow = Flow.getXUNNIFlow(i, requirement);
                requirement.getFlowsOfR().get(i).add(xunniFlow);
                flows.add(xunniFlow);
                requirement.updatePriority(i, priorityModifier);
            }

            if (flows.size()==0){
                continue;
            }
            List<Constraint> constraints = getConstraints(netContext,flows);

            for (Constraint constraint : constraints) {
                System.out.println(constraint);
            }

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
            setFlows(res,flows,i);

            // 把不能继续的移除
            Iterator<Requirements.Requirement> iterator = processedR.iterator();
            while (iterator.hasNext()){
                Requirements.Requirement requirement = iterator.next();
                if (requirement.isAccpted() || requirement.getDeadline()==i){
                    iterator.remove();
                }
            }

        }
        long time  = System.currentTimeMillis() - start;

        calendaingResult.setTotalTime(time);

        calendaingResult.setResultOneTime(netContext.getRequirements());
        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
            netContext.getNetwork().updateBandwidth(requirement.getFlowsOfR());
        }

        return calendaingResult;
    }


    @Override
    public String toString() {
        return "LPStepsOfflineScheduler{" +
                "简介=" + "离线、单时隙、LP" +
                ", constraintGenerater=" + constraintGenerater.getClass().getSimpleName() +
                '}';
    }
}
