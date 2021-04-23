package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.requirement.Requirements;

import java.util.LinkedList;
import java.util.List;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: VbvpCalendaing
 */
public class FD_VbvpEarliestOfflineScheduler extends VbvpEarliestAbstractScheduler {
    @Override
    public CalendaingResult calendaing(NetContext netContext) {

//        List<Requirements.Requirement> requirements =

        List<Requirements.Requirement> unprocessed = new LinkedList<>(netContext.getRequirements().getRequirements());
        // 根据平均传输带宽 升序
        // 离线特性
        sortRByD(unprocessed);

        CalendaingResult calendaingResult = new CalendaingResult();

        for (Requirements.Requirement next : unprocessed) {
            if (process(netContext, next)) {
                netContext.getNetwork().updateBandwidth(next.getFlowsOfR());
                calendaingResult.accept(next);
            } else {
                calendaingResult.reject(next);
            }

        }

        // 把无法完成的尽量传输
//        sortRByD(calendaingResult.getRejected());
//        tryProcess(netContext,calendaingResult.getRejected());

        return calendaingResult;
    }

    private void sortRByD(List<Requirements.Requirement> requirements){
        requirements.sort((a,b)->{
//            return (int)(b.getDemand() - a.getDemand());
//            return (int)(a.getDemand() - b.getDemand());
            // 平均带宽小的优先
//            return (int)(a.getDemand()/(a.getDeadline()-a.getReadySlot()+1) - b.getDemand()/(b.getDeadline()-b.getReadySlot()+1));
//            return a.getDeadline() - b.getDeadline();
            return (int)(a.getDemand() - b.getDemand());
        });

    }



    @Override
    public String toString() {
        return "VbvpEarliestOfflineScheduler{" +
                "简介=" + "离线、全时隙、最早最易完成" +
                "}";
    }
}
