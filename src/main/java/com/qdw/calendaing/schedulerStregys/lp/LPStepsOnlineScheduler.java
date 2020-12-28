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

/**
 * @PackageName:com.qdw.calendaing.CalendaingStregys
 * @ClassName: LPSimpleScheduler
 * @Description:
 * @date: 2020/11/30 0030 20:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LPStepsOnlineScheduler extends AbstractLPScheduler {

    private ConstraintGenerater constraintGenerater;

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        // 初始化请求的流
        netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork(),false);

        constraintGenerater = new OneSlotConstraintGenerater();

        Requirements requirements = netContext.getRequirements();
        int l = requirements.getEarliestSlot();
        int r = requirements.getLatestSlot();

        CalendaingResult calendaingResult = new CalendaingResult();
        long start = System.currentTimeMillis();
        for (int i = l; i <= r; i++) {
            List<Flow> flows = new LinkedList<>();
            for (Requirements.Requirement requirement : requirements.getRequirements()) {
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
            List<Constraint> constraints = new ArrayList<>();
            constraints.addAll(setCons(constraintGenerater.generate(netContext,flows,i, ConstraintType.LIULIANG)));
            constraints.addAll(setCons(constraintGenerater.generate(netContext,flows,i, ConstraintType.RONGLIANG)));
            constraints.addAll(setCons(constraintGenerater.generate(netContext,flows,i, ConstraintType.XUQIU)));

            for (Constraint constraint : constraints) {
                System.out.println(constraint);
            }

            String objectiveFunction = constraintGenerater.getObjFunc(netContext,flows,i)
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
            updataP(flows,i);
        }
        long time  = System.currentTimeMillis() - start;

        calendaingResult.setTotalTime(time);

        calendaingResult.setResultOneTime(netContext.getRequirements());
        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
            netContext.getNetwork().updateBandwidth(requirement.getFlowsOfR());
        }

        return calendaingResult;
    }

}
