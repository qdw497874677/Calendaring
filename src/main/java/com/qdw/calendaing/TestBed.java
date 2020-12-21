package com.qdw.calendaing;

import com.qdw.calendaing.schedulerStregys.*;
import com.qdw.calendaing.base.NetContext;

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
        // 实验次数
        int time = 200;
        List<CalendaingResult> list = new LinkedList<>();
        NetContext netContext = NetContext.getNetContext();
//        netContext.setPathProducer(new MaxBandwidthPathProducer());
        for (int i = 0; i < time; i++) {
            netContext.refresh();


//            Scheduler scheduler = new VbvpEarliestOfflineScheduler();// 离线、全时隙
//            Scheduler scheduler = new VbvpStepsOfflineScheduler();// 离线、分时隙
//            Scheduler scheduler = new VbvpEarliestOnlineScheduler();// 在线、全时隙
            Scheduler scheduler = new VbvpStepsOnlineScheduler();// 在线、分时隙

            long start = System.currentTimeMillis();
            CalendaingResult calendaingResult = scheduler.calendaing(netContext);
            calendaingResult.setTotalTime(System.currentTimeMillis()-start);
            list.add(calendaingResult);
        }
//        Calendaing calendaing = new VbvpEarliestCalendaing();
        for (CalendaingResult calendaingResult : list) {
//            System.out.println(calendaingResult);
//            System.out.println(netContext.getNetwork().getLinksInfo());
            System.out.println(calendaingResult.getAcceptRate()+" "+calendaingResult.getTotalTime()+"ms");
        }
        String averageRate = CalendaingResult.getAverageAcceptRate(list);
        System.out.println("平均完成率为:"+averageRate);
        String averageThroughputRate = CalendaingResult.getAverageThroughputRate(list);
        System.out.println("平均数据传输率为:"+averageThroughputRate);
        String averageThroughput = CalendaingResult.getAverageThroughput(list);
        System.out.println("平均吞吐量为:"+averageThroughput);
    }


    private static void test1(int time){

    }
}
