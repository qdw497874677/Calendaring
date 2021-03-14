package com.qdw.calendaing.base.requirementBase.priority;

import com.qdw.calendaing.base.Requirements;

/**
 * @Author: Quandw
 * @Description: �������ߵ��ȣ����ȿ��������ʱ��ļ�����
 */
public class MaxCS_Online_PM extends AbstractPriorityModifier {
    @Override
    public double updatePriority(int curTimeSlot, Requirements.Requirement requirement) {

        getFields(requirement);

//        double priority = 1/((double)(requirement.getDeadline() - curTimeSlot + 1)/(requirement.getDeadline() - requirement.getReadySlot() + 1)) ;
        double priority = -(requirement.getDemand() - requirement.getMeetDemand());
//        double priority = requirement.getMeetDemand() + 1/ (double)(curTimeSlot - requirement.getReadySlot() + 1)/(requirement.getDeadline() - requirement.getReadySlot() + 1) ;

        requirement.setPriority(priority);
        return priority;
    }
}
