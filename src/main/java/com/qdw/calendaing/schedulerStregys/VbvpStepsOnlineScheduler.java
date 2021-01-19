package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing.schedulerStregys
 * @ClassName: VbvpStepsOnlineScheduler
 * @Description:
 * @date: 2020/12/18 0018 10:31
 */
public class VbvpStepsOnlineScheduler extends VbvpStepsAbstractScheduler {

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        CalendaingResult calendaingResult = new CalendaingResult();
        PriorityQueue<Requirements.Requirement> curQueue = new PriorityQueue<>((a, b)->{
            return (int)(b.getPriority()-a.getPriority());
        });

        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        int l = netContext.getRequirements().getEarliestSlot();
        int r = netContext.getRequirements().getLatestSlot();

        for (int i = l; i <= r ; i++) {
            // 从未处理集合中获取可以参与计算的请求
            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==i){
                curQueue.add(unprocessed.poll());
            }
            List<Requirements.Requirement> list = new LinkedList<>();
            while (!curQueue.isEmpty()){
                Requirements.Requirement poll = curQueue.poll();
//                System.out.println(poll.getPriority());
                if (!process(netContext,poll,i)){
                    if (i==poll.getDeadline()){
                        calendaingResult.reject(poll);
                    }else {
                        list.add(poll);
                    }
                }else {
                    calendaingResult.accept(poll);
                }
            }
            curQueue.addAll(list);
        }
        while (!curQueue.isEmpty()){
            calendaingResult.reject(curQueue.poll());
        }
//        System.out.println("l:"+l+" "+"r:"+r);
//        System.out.println("@@@@"+unprocessed.size());
        return calendaingResult;
    }

    @Override
    public String toString() {
        return "VbvpStepsOnlineScheduler{" +
                "简介=" + "在线、单时隙、最早最易完成" +
                "}";
    }
}
