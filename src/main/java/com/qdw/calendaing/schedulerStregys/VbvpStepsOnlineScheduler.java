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

        // 根据开始时隙升序
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        int processSlot = netContext.getRequirements().getEarliestSlot();
        int lastSlot = netContext.getRequirements().getLatestSlot();

        // 暂时存放即将要加入curQueue的请求
        List<Requirements.Requirement> temp = new LinkedList<>();
        // 按时间顺序处理
        for (; processSlot <= lastSlot; processSlot++) {
            while (!curQueue.isEmpty()){
                Requirements.Requirement poll = curQueue.poll();
                if (!process(netContext,poll,processSlot)){
                    if (processSlot==poll.getDeadline()){
                        calendaingResult.reject(poll);
                    }else {
                        temp.add(poll);
                    }
                }else {
                    calendaingResult.accept(poll);
                }
            }
            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot() == processSlot){
                Requirements.Requirement poll = unprocessed.poll();
                if (!process(netContext,poll,processSlot)){
                    if (processSlot==poll.getDeadline()){
                        calendaingResult.reject(poll);
                    }else {
                        temp.add(poll);
                    }
                }else {
                    calendaingResult.accept(poll);
                }
            }
            curQueue.addAll(temp);
            temp.clear();
        }

        while (!curQueue.isEmpty()){
            calendaingResult.reject(curQueue.poll());
        }

//        for (int i = l; i <= r ; i++) {
//            // 从未处理集合中获取可以参与计算的请求
//            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==i){
//                curQueue.add(unprocessed.poll());
//            }
//            List<Requirements.Requirement> list = new LinkedList<>();
//            while (!curQueue.isEmpty()){
//                Requirements.Requirement poll = curQueue.poll();
////                System.out.println(poll.getPriority());
//                if (!process(netContext,poll,i)){
//                    if (i==poll.getDeadline()){
//                        calendaingResult.reject(poll);
//                    }else {
//                        list.add(poll);
//                    }
//                }else {
//                    calendaingResult.accept(poll);
//                }
//            }
//            curQueue.addAll(list);
//        }
//        while (!curQueue.isEmpty()){
//            calendaingResult.reject(curQueue.poll());
//        }
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
