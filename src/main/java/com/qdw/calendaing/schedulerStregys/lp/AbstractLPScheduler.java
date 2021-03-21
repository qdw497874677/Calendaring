package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.schedulerStregys.Scheduler;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.ConstraintGenerater;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing.CalendaingStregys
 * @ClassName: LPSimpleScheduler
 * @Description:
 * @date: 2020/11/30 0030 20:21
 */
@Data
@NoArgsConstructor
public abstract class AbstractLPScheduler implements Scheduler {

    public ConstraintGenerater constraintGenerater;

    public void setFlows(double[] res, Collection<Flow> flows, int timeSlot){
        if (flows.size()!=res.length){
//            throw new Exception("流数量和结果数量不一致");
            System.out.println("___________________________流数量和结果数量不一致");
        }
        int i = 0;
        for (Flow flow : flows) {
            BigDecimal bigDecimal = new BigDecimal(res[i++]);
            flow.setValue(bigDecimal.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue());
            if (flow.getStatus().equals(FlowStatus.XUNI)) {
                continue;
            }
            Requirements.Requirement r = flow.getThisR();
            r.setMeetDemand(r.getMeetDemand()+Math.ceil(flow.getValue()));
            // 如果满足需求就将请求状态改变
            if (r.getDemand() <= r.getMeetDemand()){
//                System.out.println("!!!!"+r.getDemand()+"  "+r.getMeetDemand());
                r.setAccpted(true);
                r.setRealFinishSlot(timeSlot);
            }
        }
    }

    public void setFlows(double[] res,NetContext netContext){
        Requirements requirements = netContext.getRequirements();
        int i = 0;
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            double sum = 0;
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    BigDecimal bigDecimal = new BigDecimal(res[i++]);
                    flow.setValue(bigDecimal.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue());
                    if (flow.getStatus().equals(FlowStatus.XUNI)) {
                        continue;
                    }
                    sum += flow.getValue();
                }
            }
            BigDecimal bigDecimal = new BigDecimal(sum);
            sum = bigDecimal.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
            requirement.setMeetDemand(sum);
            if (sum == requirement.getDemand()){
                requirement.setAccpted(true);

            }
        }
    }

    public List<Constraint> setCons(List<List<Double>> lists){
        List<Constraint> constraints = new ArrayList<>();
        for (List<Double> list : lists) {
//            System.out.println("list size:"+list.size());
            Constraint constraint = new Constraint();
//            .map((a)->{return Double.parseDouble(a+"");})
            constraint.setCoefficient(list.stream()
                    .limit(list.size()-2)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            constraint.setEquality(list.get(list.size()-2).intValue());
            constraint.setConstraintValue(list.get(list.size()-1));
            constraints.add(constraint);
//            System.out.println("@@@@:"+constraint);
        }
        return constraints;
    }

    // 更新请求优先级
    public void updateP(Collection<Flow> flows, int timeSlot, NetContext netContext){
        Requirements.Requirement pre = null;
        for (Flow flow : flows) {
            if (flow.getThisR()!=pre){
                flow.getThisR().updatePriority(timeSlot,netContext.getRequirementConfig().getPriorityModifier());
                pre = flow.getThisR();
            }
        }

    }

    public void updatePaths(NetContext netContext,int timeSlot){
        Map<String, List<Path>> pathCache = netContext.getNetwork().getPathCache();
        pathCache.clear();
        netContext.getNetwork().updatePaths(timeSlot,netContext);
    }

    public void setConstraintGenerater(ConstraintGenerater constraintGenerater){
        this.constraintGenerater = constraintGenerater;
    }

    public List<Constraint> getConstraints(NetContext netContext, List<Flow> flows){
        List<Constraint> constraints = new ArrayList<>();
//        constraintGenerater.generateAll(netContext,flows);
        constraints.addAll(setCons(constraintGenerater.generateAll(netContext,flows)));
//        constraints.addAll(setCons(constraintGenerater.generate(netContext,flows,ConstraintType.RONGLIANG)));
//        constraints.addAll(setCons(constraintGenerater.generate(netContext,flows,ConstraintType.XUQIU)));
        return constraints;
    }
}
