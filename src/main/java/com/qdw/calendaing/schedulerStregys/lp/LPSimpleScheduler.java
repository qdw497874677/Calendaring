package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.schedulerStregys.Scheduler;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class LPSimpleScheduler implements Scheduler {

    private ConstraintGenerater constraintGenerater;

    @Override
    public CalendaingResult calendaing(NetContext netContext) {

        int flowsOfAll = netContext.getRequirements().getFlowsOfAll();
        List<Constraint> constraints = new ArrayList<>();


        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.LIULIANG)));
        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.RONGLIANG)));
        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.XUQIU)));

        String objectiveFunction = constraintGenerater.getObjFunc(netContext)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))
                ;

        double[] res = LpUtil.solveLp(constraints, objectiveFunction, flowsOfAll - 2,2);


        CalendaingResult calendaingResult = new CalendaingResult();

        calendaingResult.setResultOneTime(netContext.getRequirements());

        return calendaingResult;
    }

    private void setFlows(double[] res,NetContext netContext){
        Requirements requirements = netContext.getRequirements();
        int i = 0;
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    flow.setValue(res[i++]);
                    if (flow.getStatus().equals(FlowStatus.XUNI)){
                        requirement.setAccpted(false);
                    }
                }
            }
        }
    }

    private List<Constraint> setCons(List<List<Integer>> lists){
        List<Constraint> constraints = new ArrayList<>();
        for (List<Integer> list : lists) {
            Constraint constraint = new Constraint();
            constraint.setCoefficient(list.stream()
                    .limit(list.size()-2)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            constraint.setEquality(list.get(list.size()-2));
            constraint.setConstraintValue(list.get(list.size()-1));
            constraints.add(constraint);
        }
        return constraints;
    }
}
