package com.qdw.calendaing.base.requirementBase.priority;

import com.qdw.calendaing.base.Requirements;

/**
 * @Author: Quandw
 * @Description: 最大化吞吐量——
 * @Date: 2021/1/18 0018 11:25
 */
public class MaxTP_PM extends AbstractPriorityModifier {
    @Override
    public double updatePriority(int curTimeSlot, Requirements.Requirement requirement) {

        getFields(requirement);


        double priority = (double)(curTimeSlot - readySlot + 1) / (deadline - readySlot);
//        double priority = (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) );


        requirement.setPriority(priority);
        return priority;
    }
}
