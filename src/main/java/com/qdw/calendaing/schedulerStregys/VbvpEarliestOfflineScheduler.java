package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import javafx.util.Pair;

import java.util.*;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: VbvpCalendaing
 * @Description:
 * @date: 2020/11/25 0025 20:21
 */
public class VbvpEarliestOfflineScheduler extends VbvpEarliestAbstractScheduler {
    @Override
    public CalendaingResult calendaing(NetContext netContext) {

        List<Requirements.Requirement> requirements = netContext.getRequirements().getRequirements();

        List<Requirements.Requirement> unprocessed = new LinkedList<>(requirements);
        sortRByD(unprocessed);

        CalendaingResult calendaingResult = new CalendaingResult();

        Iterator<Requirements.Requirement> iterator = unprocessed.iterator();
        while (iterator.hasNext()){
            Requirements.Requirement next = iterator.next();
            if (process(netContext, next)){
                netContext.getNetwork().updateBandwidth(next.getFlowsOfR());
                calendaingResult.accept(next);
            }else {
                calendaingResult.reject(next);
            }
        }

        // 把无法完成的尽量传输
        sortRByD(calendaingResult.getRejected());
        for (Requirements.Requirement requirement : calendaingResult.getRejected()) {
            tryProcess(netContext,requirement);
        }

        return calendaingResult;
    }

    private void sortRByD(List<Requirements.Requirement> requirements){
        requirements.sort((a,b)->{
//            return (int)(b.getDemand() - a.getDemand());
//            return (int)(a.getDemand() - b.getDemand());
            // 平均带宽小的优先
            return (int)(a.getDemand()/(a.getDeadline()-a.getReadySlot()+1) - b.getDemand()/(b.getDeadline()-b.getReadySlot()+1));
        });

    }

    private void tryProcess(NetContext netContext,Requirements.Requirement requirement){
        int l = requirement.getReadySlot();
        int r = requirement.getDeadline();
        for (int i = l; i <= r; i++) {
            Pair<Path, Double> oneStepPath = getOneSlotPath(netContext, requirement, i);
            if (oneStepPath!=null && oneStepPath.getValue()>0){
                requirement.addFlow(i,FlowStatus.ZHENGCHANG,oneStepPath.getKey(),oneStepPath.getValue());
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }
        }

    }

}
