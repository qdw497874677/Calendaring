package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.ConstraintGenerater;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.DefaultConstraintGenerater;
import com.qdw.lpnet.LpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class LPSimpleOfflineScheduler extends AbstractLPScheduler {

    {
        setConstraintGenerater(new DefaultConstraintGenerater());
    }


    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        // 初始化请求的流
        netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork(),true);

        List<Flow> flows = netContext.getRequirements().getAllFlows();

        // 获取约束
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

        CalendaingResult calendaingResult = new CalendaingResult();
        calendaingResult.setTotalTime(time);
        setFlows(res,netContext);
        calendaingResult.setResultOneTime(netContext.getRequirements());
        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
            netContext.getNetwork().updateBandwidth(requirement.getFlowsOfR());
        }

        return calendaingResult;
    }


    @Override
    public String toString() {
        return "LPSimpleOfflineScheduler{" +
                "简介=" + "离线、全时隙、LP" +
                ", constraintGenerater=" + constraintGenerater.getClass().getSimpleName() +
                '}';
    }
}
