package com.qdw.calendaing;

//import com.qdw.calendaing.base.RandomRProducer;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.pathBase.BdwLimitProducer;
import com.qdw.calendaing.base.pathBase.ShortestMaxBandwidthPathProducer;
import com.qdw.calendaing.base.pathBase.ShortestMaxBandwidthPathWithBdwLimitProducer;
import com.qdw.calendaing.base.requirementBase.RandomRProducer;
import com.qdw.calendaing.base.pathBase.MaxBandwidthPathProducer;
import com.qdw.calendaing.base.pathBase.kpaths.SimpleKPathsProducer;
import com.qdw.calendaing.base.requirementBase.RandomReqWithBwLimitProducer;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_PM;
import com.qdw.calendaing.base.requirementBase.priority.MaxTP_PM;
import com.qdw.calendaing.dataCollecting.ToFileDataCollecter;
import com.qdw.calendaing.schedulerStregys.*;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.schedulerStregys.PBSP_FDBRR.lp.FD_LPStepsOfflineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.LPSimpleOfflineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.LPStepsOfflineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.LPStepsOnlineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.LPWithBdwLimitScheduler;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.WithBdwLimitConstraintGenerater;

import java.util.LinkedList;
import java.util.List;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: TestBed
 * @Description:
 * @date: 2020/11/30 0030 10:23
 */
public class TestBed {


    public static void main(String[] args) {
//        test(50);
//        test(100);
//        test(200);
//        test(300);
//        test(400);
        test(500);
    }



    public static void test(int numOfR) {

        //        String topoStr = "0-2,0-4,2-4,1-2,1-3,4-3,4-11,1-5,3-5,5-6,5-7,11-10,11-6,6-7,6-8,8-10,10-9,9-12,10-15,8-15,6-16,7-21,21-23,21-24,23-24,23-16,23-20,16-20,20-25,16-13,25-18,18-19,25-17,17-19,14-17,13-17,12-14,12-13,15-13,12-15,19-22,22-18";
//        int numOfNode = 26;
//        double capacity = 40.0;

        // ESNET 57
//        String topoStr = "0-1,1-2,2-3,3-4,5-6,6-7,7-8,8-9,0-9,4-10,10-11,11-12,12-13,7-13,12-14,14-15,15-16,17-18,18-19,19-20,7-20,17-21,21-22,22-23,23-24,24-25,25-26,26-27,19-27,23-28,28-29,29-30,30-31,31-32,32-33,33-34,34-35,35-36,23-36,34-37,37-38,38-39,39-40,40-41,26-41,32-42,42-43,43-44,44-45,45-46,46-47,47-48,48-49,49-50,40-49,40-50,44-51,51-54,54-55,55-56,47-52,52-53,53-56";
//        int numOfNode = 57;
//        double capacity = 40.0;

        // ESNET 21
        String topoStr = "0-1,0-3,0-5,0-6,0-12,1-5,2-3,3-4,3-6,3-8,5-6,6-7,6-10,7-8,8-9,9-15,9-10,10-12,11-12,12-13,12-16,12-17,13-14,13-15,14-15,14-17,9-15,15-17,16-17,16-18,16-20,15-17,16-17,17-18,18-19,18-20";
        int numOfNode = 21;
        double capacity = 40.0;

        PathConfig pathConfig = new PathConfig(
                6,
                10,
//                new MaxBandwidthPathProducer(),
//                new ShortestMaxBandwidthPathWithBdwLimitProducer(),
                new BdwLimitProducer(new ShortestMaxBandwidthPathProducer()),
                new SimpleKPathsProducer()
        );

        RequirementConfig requirementConfig = new RequirementConfig(
//                600,
                numOfR,
                0,
                19,
                6,
//                new RandomRProducer(),
                new RandomReqWithBwLimitProducer(),

                new MaxCS_PM()
//                new MaxTP_PM()
        );

        NetContext netContext = NetContext.getNetContext(
                // 网络链路拓扑
                topoStr,
                // 节点数量
                numOfNode,
                // 链路容量
                capacity,
                pathConfig,
                requirementConfig

        );

        // 实验次数
        int time = 1;
        List<CalendaingResult> list = new LinkedList<>();
        StringBuilder print = new StringBuilder();
//        netContext.setPathProducer(new MaxBandwidthPathProducer());
        Scheduler scheduler = null;
        for (int i = 0; i < time; i++) {
            netContext.refresh();
            // 设置为多路
            netContext.setMulti(true);

//            scheduler = new VbvpEarliestOfflineScheduler();// 离线、全时隙
//            scheduler = new VbvpStepsOfflineScheduler();// 离线、分时隙
//            scheduler = new VbvpEarliestOnlineScheduler();// 在线、全时隙
//            scheduler = new VbvpStepsOnlineScheduler();// 在线、分时隙


//             scheduler = new LPSimpleOfflineScheduler();// 离线、全时隙、LP
//            scheduler = new LPWithBdwLimitScheduler(new LPSimpleOfflineScheduler());

//            scheduler = new LPStepsOfflineScheduler();// 离线、分时隙、LP
//            scheduler = new LPWithBdwLimitScheduler(new LPStepsOfflineScheduler());

//             scheduler = new LPStepsOnlineScheduler();// 在线、分时隙、LP
//             scheduler = new LPWithBdwLimitScheduler(new LPStepsOnlineScheduler());// 在线、分时隙、LP

//            scheduler = new FD_LPStepsOfflineScheduler();// 离线、分时隙、LP
            scheduler = new LPWithBdwLimitScheduler(new FD_LPStepsOfflineScheduler());

            long start = System.currentTimeMillis();
            CalendaingResult calendaingResult = scheduler.calendaing(netContext);
            calendaingResult.setTotalTime(System.currentTimeMillis()-start);
            list.add(calendaingResult);
        }
//        Calendaing calendaing = new VbvpEarliestCalendaing();

        long avgTime = 0;
        for (CalendaingResult calendaingResult : list) {
//            System.out.println(calendaingResult);
//            System.out.println(netContext.getNetwork().getLinksInfo());
            System.out.println(calendaingResult.getAcceptRate()+" "+calendaingResult.getTotalTime()+"ms");
            avgTime += calendaingResult.getTotalTime();
        }

        print.append(netContext.toString()).append("\n");
        print.append("调度算法:").append(scheduler.toString()).append("\n");
        print.append("实验次数:").append(time).append("\n");
        print.append("平均一次实验计算用时:").append(avgTime/time).append("ms\n");

        String averageRate = CalendaingResult.getAverageAcceptRate(list);
        print.append("平均完成率为:").append(averageRate).append("\n");
        String averageThroughputRate = CalendaingResult.getAverageThroughputRate(list);
        print.append("平均数据传输率为:").append(averageThroughputRate).append("\n");
        String averageThroughput = CalendaingResult.getAverageThroughput(list);
        print.append("平均吞吐量为:").append(averageThroughput).append("\n");
        String averageReservedRateButRejected = CalendaingResult.getAverageReservedRateButRejected(list);
        print.append("平均未完成请求的传输率:").append(averageReservedRateButRejected);


        ToFileDataCollecter toFileDataCollecter = new ToFileDataCollecter("test.txt");
        toFileDataCollecter.addData(print.toString());

        System.out.println(print);



    }


    private static void test1(int time){

    }
}
