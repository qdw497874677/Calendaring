package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.ConstraintGenerater;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.OneSlotConstraintGenerater;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        int l = requirements.getEarliestSlot();
        int r = requirements.getLatestSlot();

        CalendaingResult calendaingResult = new CalendaingResult();
        long start = System.currentTimeMillis();
        for (int i = l; i <= r; i++) {
            List<Flow> flows = new LinkedList<>();
            for (Requirements.Requirement requirement : requirements.getRequirements()) {
                // 如果还没有完全被满足，且有当前时隙初始的流
                if (!requirement.isAccpted() && requirement.getFlowsOfR().containsKey(i)){
                    flows.addAll(requirement.getFlowsOfR().get(i));
                    Flow xunniFlow = Flow.getXUNNIFlow(i, requirement);
                    requirement.getFlowsOfR().get(i).add(xunniFlow);
                    flows.add(xunniFlow);
                }
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
            updateP(flows,i,netContext);

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
