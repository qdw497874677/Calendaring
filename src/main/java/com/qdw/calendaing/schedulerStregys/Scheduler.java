package com.qdw.calendaing.schedulerStregys;

import com.qdw.calendaing.CalendaingResult;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Network;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: Calendaing
 * @Description:
 * @date: 2020/11/25 0025 19:26
 */
public interface Scheduler {

    CalendaingResult calendaing(NetContext netContext);
}
