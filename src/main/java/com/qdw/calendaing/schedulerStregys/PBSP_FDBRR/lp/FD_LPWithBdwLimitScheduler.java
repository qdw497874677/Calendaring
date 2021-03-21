package com.qdw.calendaing.schedulerStregys.PBSP_FDBRR.lp;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.schedulerStregys.lp.AbstractLPScheduler;
import com.qdw.calendaing.schedulerStregys.lp.constraintGenerater.WithBdwLimitConstraintGenerater;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/15 0015 22:31
 */
public class FD_LPWithBdwLimitScheduler extends AbstractLPScheduler {
    private AbstractLPScheduler scheduler;

    public FD_LPWithBdwLimitScheduler(AbstractLPScheduler scheduler){
        this.scheduler = scheduler;
        scheduler.setConstraintGenerater(new WithBdwLimitConstraintGenerater(scheduler.getConstraintGenerater()));
    }

    @Override
    public CalendaingResult calendaing(NetContext netContext) {
        return scheduler.calendaing(netContext);
    }


    @Override
    public String toString() {
        return "LPWithBdwLimitScheduler{" +
                "scheduler=" + scheduler.toString() +
                '}';
    }
}
