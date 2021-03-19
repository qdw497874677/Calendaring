package com.qdw.calendaing.base.requirementBase.priority;

import com.qdw.calendaing.base.requirement.Requirements;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/18 0018 11:14
 */
public interface PriorityModifier {

    double updatePriority(int curTimeSlot, Requirements.Requirement requirement);

}
