package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.requirementBase.priority.PriorityModifier;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: VbvpSmallestCalendaing
 * @Description:
 * @date: 2020/11/25 0025 20:21
 */
public class FD_VbvpStepsOfflineScheduler extends VbvpStepsAbstractScheduler {
    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        CalendaingResult calendaingResult = new CalendaingResult();
        int l = netContext.getRequirements().getEarliestSlot();
        int r = netContext.getRequirements().getLatestSlot();
        int curSlot = l;
        // ÿ�������� ���ȼ��ߵ�����
        PriorityQueue<Requirements.Requirement> curQueue = new PriorityQueue<>((a, b)->{
            return b.getPriority().compareTo(a.getPriority());
        });

        // ���ݿ�ʼʱ϶����Ϊ�˷�����ȡ����
        Queue<Requirements.Requirement> unprocessed = netContext.getRequirements().getRequirements().stream().sorted((a, b) -> {
            return a.getReadySlot() - b.getReadySlot();
        }).collect(Collectors.toCollection(LinkedList::new));
        PriorityModifier priorityModifier = netContext.getRequirementConfig().getPriorityModifier();
        List<Requirements.Requirement> list = new LinkedList<>();
        for (;; curSlot++) {
            // ��δ�������л�ȡ���Բ�����������

            while (!unprocessed.isEmpty() && unprocessed.peek().getReadySlot()==curSlot){
                list.add(unprocessed.poll());

//                curQueue.add(unprocessed.poll());
            }
            for (Requirements.Requirement requirement : list) {
                requirement.updatePriority(curSlot, priorityModifier);
            }

            curQueue.addAll(list);
            list.clear();
            while (!curQueue.isEmpty()){
                Requirements.Requirement poll = curQueue.poll();
//                System.out.println(poll.getPriority());
                if (!process(netContext,poll,curSlot)){
                    list.add(poll);
                }else {
                    poll.setRealFinishSlot(curSlot);
                    calendaingResult.accept(poll);
                }
            }
//            list.add(curQueue);
            if (list.isEmpty() && unprocessed.isEmpty()){
                break;
            }
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
