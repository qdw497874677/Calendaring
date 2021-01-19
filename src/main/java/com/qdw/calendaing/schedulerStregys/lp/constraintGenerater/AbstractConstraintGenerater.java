package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.CalendaingStregys
 * @ClassName: LPSimpleScheduler
 * @Description:
 * @date: 2020/11/30 0030 20:21
 */
@Data
@AllArgsConstructor
public abstract class AbstractConstraintGenerater implements ConstraintGenerater {



    @Override
    public List<List<Integer>> generateAll(NetContext netContext, Collection<Flow> flows) {
        List<List<Integer>> res = new LinkedList<>();
        res.addAll(generateOne(netContext,flows,ConstraintType.RONGLIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.LIULIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.XUQIU));
        return res;
    }


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
