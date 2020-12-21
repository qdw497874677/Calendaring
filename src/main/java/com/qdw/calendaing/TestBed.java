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
        // ʵ�����
        int time = 200;
        List<CalendaingResult> list = new LinkedList<>();
        NetContext netContext = NetContext.getNetContext();
//        netContext.setPathProducer(new MaxBandwidthPathProducer());
        for (int i = 0; i < time; i++) {
            netContext.refresh();


//            Scheduler scheduler = new VbvpEarliestOfflineScheduler();// ���ߡ�ȫʱ϶
//            Scheduler scheduler = new VbvpStepsOfflineScheduler();// ���ߡ���ʱ϶
//            Scheduler scheduler = new VbvpEarliestOnlineScheduler();// ���ߡ�ȫʱ϶
            Scheduler scheduler = new VbvpStepsOnlineScheduler();// ���ߡ���ʱ϶

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
        System.out.println("ƽ�������Ϊ:"+averageRate);
        String averageThroughputRate = CalendaingResult.getAverageThroughputRate(list);
        System.out.println("ƽ�����ݴ�����Ϊ:"+averageThroughputRate);
        String averageThroughput = CalendaingResult.getAverageThroughput(list);
        System.out.println("ƽ��������Ϊ:"+averageThroughput);
    }


    private static void test1(int time){

    }
}
