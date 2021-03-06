package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;

import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpEarliestOnlineScheduler
 * @Description:
 * @date: 2020/12/18 0018 11:07
 */
public class VbvpEarliestOnlineScheduler extends VbvpEarliestAbstractScheduler {

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        Requirements requirements = netContext.getRequirements();
        List<Requirements.Requirement> requirementsList = requirements.getRequirements();

        sortRBySlot(requirementsList);

        CalendaingResult calendaingResult = new CalendaingResult();

        for (Requirements.Requirement requirement : requirementsList) {
            boolean process = process(netContext, requirement);
            if (process){
                calendaingResult.accept(requirement);
                netContext.getNetwork().updateBandwidth(requirement.getFlowsOfR());
            }else {
                calendaingResult.reject(requirement);
            }
        }

        tryProcess(netContext,calendaingResult.getRejected());
        return calendaingResult;
    }



    private void sortRBySlot(List<Requirements.Requirement> requirements) {
        requirements.sort((a,b)->{
            return a.getReadySlot() - b.getReadySlot();
        });
    }

    @Override
    public String toString() {
        return "VbvpEarliestOnlineScheduler{" +
                "简介=" + "在线、全时隙、最早最易完成" +
                "}";
    }
}
