package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Iterator;

/**
 * @PackageName:com.qdw.calendaing.CalendaingStregys
 * @ClassName: LPSimpleScheduler
 * @Description:
 * @date: 2020/11/30 0030 20:21
 */
@Data
@AllArgsConstructor
public abstract class AbstractConstraintGenerater implements ConstraintGenerater {





    Integer getCost(Flow flow,int plusValue,int valueOfXUNI){

        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
            return flow.getPath().getPath().size() + plusValue;
        }else {
            return valueOfXUNI;
        }
    }
    Integer getCost(Flow flow){
        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
            return flow.getPath().getPath().size();
        }else {
            return 1000;
        }
    }


}
