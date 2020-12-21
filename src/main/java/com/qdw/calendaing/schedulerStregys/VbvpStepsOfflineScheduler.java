package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.constant.FlowStatus;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: VbvpSmallestCalendaing
 * @Description:
 * @date: 2020/11/25 0025 20:21
 */
public class VbvpStepsOfflineScheduler extends VbvpStepsAbstractScheduler {
    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        CalendaingResult calendaingResult = new CalendaingResult();
        PriorityQueue<Requirements.Requirement> curQueue = new PriorityQueue<>((a,b)->{
            return (int)(b.getPriority()-a.getPriority());
        });

        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        int l = netContext.getRequirements().getEarliestSlot();
        int r = netContext.getRequirements().getLatestSlot();

        for (int i = l; i <= r ; i++) {
            // ��δ�������л�ȡ���Բ�����������
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





}
