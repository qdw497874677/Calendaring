package com.qdw.calendaing.base.requirementBase.priority;

import com.qdw.calendaing.base.requirement.Requirements;

/**
 * @Author: Quandw
 * @Description: 最大化完成率——时间越早，满足的数据量越大，优先级越高
 * @Date: 2021/1/18 0018 11:25
 */
public class MaxCS_PM extends AbstractPriorityModifier {
    @Override
    public double updatePriority(int curTimeSlot, Requirements.Requirement requirement) {

        getFields(requirement);

//        double priority = - (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) ) ;
        double priority = -(demand - meetDemand) / (1 + (double)(curTimeSlot-readySlot + 1)/(deadline - readySlot + 1) ) ;
//        double priority = -(demand - meetDemand);
//        double priority = - (demand - meetDemand) ;

//        double priority = - demand;
//        double priority = (curTimeSslot - deadline) ;
//        double priority =  (1 + (double)(curTimeSlot - readySlot + 1)/(deadline - readySlot + 1) )/(demand - meetDemand) ;
//        double priority =  (1 + (double)(curTimeSlot - readySlot + 1)/(deadline - readySlot + 1) ) * ((demand)/(demand - meetDemand))  ;
//        double priority = curTimeSlot>deadline?(-(demand-meetDemand)):demand/(demand - meetDemand) ;
//        double priority = -requirement.getDemand() - requirement.getDeadline() ;
//        double priority = -(curTimeSlot+1-requirement.getReadySlot()) ;

        // LP在按时完成传输率、带宽消耗上表现好
//        double priority =  demand*(1 + (double)(curTimeSlot - readySlot + 1)/(deadline - readySlot + 1) )/(demand - meetDemand) ;
//        double priority =  (1 + (double)(curTimeSlot - readySlot + 1)/(deadline - readySlot + 1) )/(demand - meetDemand) ;
//        double priority =  (1000-(demand - meetDemand)) ;

//        double priority = 1/((demand - meetDemand)*10);
//        double priority = 1/(demand - meetDemand);
//        double priority = 1/demand;
//        double priority =  (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) )/(demand - meetDemand) ;

//        double priority =  ( 1+(double)(curTimeSlot)/(deadline) )/(demand - meetDemand) ;
        
//        double priority =  ((1 + (double)(curTimeSlot)/(deadline) )) * (demand/(demand - meetDemand)) ;
        requirement.setPriority(priority);
        return priority;
    }
}
