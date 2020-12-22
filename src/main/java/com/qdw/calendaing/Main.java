package com.qdw.calendaing;

import com.qdw.calendaing.schedulerStregys.Scheduler;
import com.qdw.calendaing.base.MaxBandwidthPathProducer;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.schedulerStregys.VbvpEarliestOfflineScheduler;
import com.qdw.calendaing.schedulerStregys.VbvpEarliestOnlineScheduler;
import com.qdw.calendaing.schedulerStregys.VbvpStepsOnlineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.DefaultConstraintGenerater;
import com.qdw.calendaing.schedulerStregys.lp.LPSimpleOfflineScheduler;

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

        Scheduler scheduler = new VbvpEarliestOfflineScheduler();
//        Scheduler scheduler = new VbvpStepsOfflineScheduler();
//        Scheduler scheduler = new VbvpEarliestOnlineScheduler();
//        Scheduler scheduler = new VbvpStepsOnlineScheduler();
//        Scheduler scheduler = new LPSimpleOfflineScheduler(new DefaultConstraintGenerater());

//        Scheduler scheduler = new LPSimpleScheduler(new DefaultConstraintGenerater());

        CalendaingResult calendaingResult = scheduler.calendaing(netContext);
        System.out.println(calendaingResult);
        System.out.println(netContext.getNetwork().getLinksInfo());
        System.out.println(calendaingResult.getAcceptRate());
        System.out.println(calendaingResult.getThroughputRate());
    }
}
