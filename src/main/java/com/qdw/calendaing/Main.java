package com.qdw.calendaing;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.schedulerStregys.Scheduler;
import com.qdw.calendaing.base.MaxBandwidthPathProducer;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.schedulerStregys.lp.LPStepsOnlineScheduler;

import java.util.List;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: Main
 * @Description:
 * @date: 2020/11/26 0026 15:35
 */
public class Main {
    public static void main(String[] args) {

        NetContext netContext = NetContext.getNetContext();
        netContext.setPathProducer(new MaxBandwidthPathProducer());
        netContext.refresh();

//        Scheduler scheduler = new VbvpEarliestOfflineScheduler();
//        Scheduler scheduler = new VbvpStepsOfflineScheduler();
//        Scheduler scheduler = new VbvpEarliestOnlineScheduler();
//        Scheduler scheduler = new VbvpStepsOnlineScheduler();
//        Scheduler scheduler = new LPSimpleOfflineScheduler();
        Scheduler scheduler = new LPStepsOnlineScheduler();

//        Scheduler scheduler = new LPSimpleScheduler(new DefaultConstraintGenerater());

        CalendaingResult calendaingResult = scheduler.calendaing(netContext);
        System.out.println(calendaingResult);
        System.out.println(netContext.getNetwork().getLinksInfo());
        System.out.println(calendaingResult.getAcceptRate());
        System.out.println(calendaingResult.getThroughputRate());
        System.out.println("ºÄÊ±£º"+calendaingResult.getTotalTime()+"ms");

        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
            if (requirement.getMeetDemand()>requirement.getDemand()){
                System.out.println("!!!"+requirement.getMeetDemand() + "  " + requirement.getDemand());
                for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                    for (Flow flow : flows) {
                        System.out.print(flow+" "+flow.getValue());
                        if (flow.getStatus().equals(FlowStatus.XUNI)){
                            System.out.println(" ÊÇÐéÄâÁ÷  ");
                        }
                    }
                }
            }
        }
    }
}
