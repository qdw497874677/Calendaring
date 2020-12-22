package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.Link;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.schedulerStregys.Scheduler;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
public class LPSimpleOfflineScheduler implements Scheduler {

    private ConstraintGenerater constraintGenerater;

    @Override
    public CalendaingResult calendaing(NetContext netContext) {


        List<Constraint> constraints = new ArrayList<>();


        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.LIULIANG)));
        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.RONGLIANG)));
        constraints.addAll(setCons(constraintGenerater.generate(netContext, ConstraintType.XUQIU)));

        for (Constraint constraint : constraints) {
            System.out.println(constraint);
        }


        String objectiveFunction = constraintGenerater.getObjFunc(netContext)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "))
                ;
        System.out.println(objectiveFunction);
        int flowsOfAll = netContext.getRequirements().getFlowsOfAll();
        System.out.println("flowsOfAll:"+flowsOfAll);


//        constraints.clear();
//        Constraint constraint1 = new Constraint();
//        constraint1.setCoefficient("120 210");
//        constraint1.setEquality(1);
//        constraint1.setConstraintValue(15000);
//        Constraint constraint2 = new Constraint();
//        constraint2.setCoefficient("110 30");
//        constraint2.setEquality(1);
//        constraint2.setConstraintValue(4000);
//        Constraint constraint3 = new Constraint();
//        constraint3.setCoefficient("1 1");
//        constraint3.setEquality(1);
//        constraint3.setConstraintValue(75);
//
//        constraint3.setCoefficient("0 0");
//        constraint3.setEquality(1);
//        constraint3.setConstraintValue(75);
//        constraints.add(constraint1);
//        constraints.add(constraint2);
//        constraints.add(constraint3);
//        objectiveFunction = "143 60";
//        flowsOfAll = 2;
        long start = System.currentTimeMillis();
        double[] res = LpUtil.solveLp(constraints, objectiveFunction, flowsOfAll,2);
        long time  = System.currentTimeMillis() - start;
        System.out.println(Arrays.toString(res));

        CalendaingResult calendaingResult = new CalendaingResult();
        calendaingResult.setTotalTime(time);
        setFlows(res,netContext);
        calendaingResult.setResultOneTime(netContext.getRequirements());
        return calendaingResult;
    }

    private void setFlows(double[] res,NetContext netContext){
        Requirements requirements = netContext.getRequirements();
        int i = 0;
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            double sum = 0;
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    flow.setValue(res[i]);
                    if (flow.getStatus().equals(FlowStatus.XUNI)){
                        requirement.setAccpted(false);
                    }else {
                        sum += res[i];
                    }
                    i++;
                }
            }

            BigDecimal bigDecimal = new BigDecimal(sum);
            sum = bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            requirement.setMeetDemand(sum);
            if (sum == requirement.getDemand()){
                requirement.setAccpted(true);
            }
        }
    }

    private List<Constraint> setCons(List<List<Integer>> lists){
        List<Constraint> constraints = new ArrayList<>();

        for (List<Integer> list : lists) {
//            System.out.println("list size:"+list.size());
            Constraint constraint = new Constraint();
            constraint.setCoefficient(list.stream()
                    .limit(list.size()-2)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            constraint.setEquality(list.get(list.size()-2));
            constraint.setConstraintValue(list.get(list.size()-1));
            constraints.add(constraint);
//            System.out.println("@@@@:"+constraint);
        }
        return constraints;
    }
}
