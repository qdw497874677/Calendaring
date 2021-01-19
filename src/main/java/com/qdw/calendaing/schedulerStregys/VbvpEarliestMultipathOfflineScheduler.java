package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class VbvpEarliestMultipathOfflineScheduler extends VbvpEarliestAbstractScheduler {
    @Override
    public CalendaingResult calendaing(NetContext netContext) {

        List<Requirements.Requirement> requirements = netContext.getRequirements().getRequirements();

        List<Requirements.Requirement> unprocessed = new LinkedList<>(requirements);
        sortRByD(unprocessed);

        CalendaingResult calendaingResult = new CalendaingResult();

//        for (Requirements.Requirement next : unprocessed) {
//            if (processByMultipath(netContext, next)) {
//                netContext.getNetwork().updateBandwidth(next.getFlowsOfR());
//                calendaingResult.accept(next);
//            } else {
//                calendaingResult.reject(next);
//            }
//        }

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


    @Override
    public String toString() {
        return "VbvpEarliestOfflineScheduler{" +
                "简介=" + "离线、全时隙、最早最易完成" +
                "}";
    }
}
