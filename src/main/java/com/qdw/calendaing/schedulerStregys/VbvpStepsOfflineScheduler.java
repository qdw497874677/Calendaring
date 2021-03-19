package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.*;
import com.qdw.calendaing.base.requirement.Requirements;

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
        int l = netContext.getRequirements().getEarliestSlot();
        int r = netContext.getRequirements().getLatestSlot();
        int curSlot = l;
        // ÿ�������� ���ȼ��ߵ�����
        PriorityQueue<Requirements.Requirement> curQueue = new PriorityQueue<>((a, b)->{


//            if (a.getDeadline()==curSlot && b.getDeadline()>curSlot){
//                System.out.println("#@#@#@#@#@");
//                return 1;
//            }else if (b.getDeadline()==curSlot && a.getDeadline()>curSlot){
//                System.out.println("#@#@#@#@#@");
//                return -1;
//            }
//            System.out.println("&&&&&&&&&&&&&");
            return (int)(b.getPriority()-a.getPriority());
        });

        // ���ݿ�ʼʱ϶����Ϊ�˷�����ȡ����
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));


        for (; curSlot <= r ; curSlot++) {
            // ��δ�������л�ȡ���Բ�����������
            System.out.println();
            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==curSlot){
                curQueue.add(unprocessed.poll());
            }

            List<Requirements.Requirement> list = new LinkedList<>();
            while (!curQueue.isEmpty()){
                Requirements.Requirement poll = curQueue.poll();
//                System.out.println(poll.getPriority());
                if (!process(netContext,poll,curSlot)){
                    if (curSlot==poll.getDeadline()){
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
        return "VbvpStepsOfflineScheduler{" +
                "���=" + "���ߡ���ʱ϶�������������" +
                "}";
    }
}
