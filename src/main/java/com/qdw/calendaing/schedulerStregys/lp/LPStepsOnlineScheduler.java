package com.qdw.calendaing.schedulerStregys.lp;

import com.pranav.pojo.Constraint;
import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_Online_PM;
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

/**
 * @PackageName:com.qdw.calendaing.CalendaingStregys
 * @ClassName: LPSimpleScheduler
 * @Description:
 * @date: 2020/11/30 0030 20:21
 */
@Data
@AllArgsConstructor
public class LPStepsOnlineScheduler extends AbstractLPScheduler {

    {
        setConstraintGenerater(new OneSlotConstraintGenerater());
    }

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        // 初始化请求的流
        netContext.getRequirements().initializeFlows(netContext.getPathConfig(),netContext.getNetwork(),false);

        // 根据开始时隙升序
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        int processSlot = netContext.getRequirements().getEarliestSlot();
        int lastSlot = netContext.getRequirements().getLatestSlot();

        // 设置优先级更新器
//        PriorityModifier priorityModifier = new MaxCS_Online_PM();
        PriorityModifier priorityModifier = new MaxCS_PM();
        List<Requirements.Requirement> processingR = new ArrayList<>();

        CalendaingResult calendaingResult = new CalendaingResult();
        long start = System.currentTimeMillis();
        for (; processSlot <= lastSlot; processSlot++) {
            // 先处理PR中的
            if (!processingR.isEmpty()){

                // 更新优先级
                for (Requirements.Requirement requirement : processingR) {
                    requirement.updatePriority(processSlot, priorityModifier);
                }

                lp(processingR, processSlot, netContext);
                List<Requirements.Requirement> temp = new ArrayList<>();
                for (Requirements.Requirement requirement : processingR) {
                    if (!check(requirement,calendaingResult,processSlot)){
                        temp.add(requirement);

                    }
                }
                processingR = new ArrayList<>(temp);
            }

            while (!unprocessed.isEmpty() && processSlot == unprocessed.peek().getReadySlot()){
                Requirements.Requirement poll = unprocessed.poll();
                lp(Collections.singletonList(poll),processSlot,netContext);
                if (!check(poll,calendaingResult,processSlot)){
                    processingR.add(poll);
                }
//                // 更新带宽
//                netContext.getNetwork().updateBandwidth(poll.getFlowsOfR().get(processSlot));
            }

        }

        long time  = System.currentTimeMillis() - start;

        calendaingResult.setTotalTime(time);

//        calendaingResult.setResultOneTime(netContext.getRequirements());
        return calendaingResult;
    }

    // 如果返回false表示需要继续
    public boolean check(Requirements.Requirement r,CalendaingResult calendaingResult,int curTime){
        if (r.isAccpted()){
            calendaingResult.accept(r);
        }else if (r.getDeadline() <= curTime){
            calendaingResult.reject(r);
        }else {
            // 如果没有完成需求，且还没有到截止时隙，就返回false
            return false;
        }
        return true;
    }

    public void lp(Collection<Requirements.Requirement> rs, int timeSlot, NetContext netContext){
        if (rs==null || rs.size()==0){
            return;
        }
        List<Flow> flows = new LinkedList<>();
        for (Requirements.Requirement requirement : rs) {
            if (!requirement.isAccpted() && requirement.getFlowsOfR().containsKey(timeSlot)){
                flows.addAll(requirement.getFlowsOfR().get(timeSlot));
                Flow xunniFlow = Flow.getXUNNIFlow(timeSlot, requirement);
                requirement.getFlowsOfR().get(timeSlot).add(xunniFlow);
                flows.add(xunniFlow);
            }
        }
        if (flows.size()==0){
            return;
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
        setFlows(res,flows,timeSlot);
//        updateP(flows,timeSlot,netContext);

        // 当前时隙计算的流量更新到网络中
        for (Requirements.Requirement requirement : rs) {
            List<Flow> flowList = requirement.getFlowsOfR().get(timeSlot);
            netContext.getNetwork().updateBandwidth(flowList);
        }
    }

    @Override
    public String toString() {
        return "LPStepsOnlineScheduler{" +
                "简介=" + "在线、单时隙、LP" +
                ", constraintGenerater=" + constraintGenerater.getClass().getSimpleName() +
                '}';
    }
}
