package com.qdw.calendaing.base.requirementBase.priority;

import com.qdw.calendaing.base.requirement.Requirements;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/18 0018 11:29
 */
public abstract class AbstractPriorityModifier implements PriorityModifier {
    double demand;
    double meetDemand;
    int deadline;
    int readySlot;


    public void getFields(Requirements.Requirement requirement){
        this.demand = requirement.getDemand();
        this.meetDemand = requirement.getMeetDemand();
        this.deadline = requirement.getDeadline();
        this.readySlot = requirement.getReadySlot();
    }
}
