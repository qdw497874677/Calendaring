package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
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
    public List<List<Double>> generateAll(NetContext netContext, Collection<Flow> flows) {
        List<List<Double>> res = new LinkedList<>();
        res.addAll(generateOne(netContext,flows,ConstraintType.RONGLIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.LIULIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.XUQIU));
        return res;
    }


    public Double getCost(Flow flow,double plusValue,int valueOfXUNI){

        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
            // 如果是正常流，则将所对应的请求的优先级也介入流的权值的计算
            return flow.getPath().getPath().size() * plusValue;
        }else {
            return (double)valueOfXUNI;
//            return -plusValue * 100;
//            return (int)(Integer.MAX_VALUE/(flow.getThisR().getDemand() - flow.getThisR().getMeetDemand()));
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
