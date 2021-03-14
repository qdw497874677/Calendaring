package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/15 0015 17:52
 */
public class WithBdwLimitConstraintGenerater extends AbstractConstraintGenerater {

    ConstraintGenerater constraintGenerater;
    public WithBdwLimitConstraintGenerater(ConstraintGenerater constraintGenerater){
        this.constraintGenerater = constraintGenerater;
    }

    @Override
    public List<List<Integer>> generateAll(NetContext netContext, Collection<Flow> flows) {
        List<List<Integer>> res = new LinkedList<>();
        res.addAll(generateOne(netContext,flows,ConstraintType.RONGLIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.LIULIANG));
        res.addAll(generateOne(netContext,flows,ConstraintType.XUQIU));
        res.addAll(generateOne(netContext,flows,ConstraintType.MAXBDW));
        return res;
    }

    @Override
    public List<List<Integer>> generateOne(NetContext netContext, Collection<Flow> flows, ConstraintType constraintType) {
        if (constraintType.equals(ConstraintType.MAXBDW)){
            return getMBCons(netContext,flows);
        }
        return constraintGenerater.generateOne(netContext,flows,constraintType);
    }



    @Override
    public List<Integer> getObjFunc(NetContext netContext, Collection<Flow> flows) {
        return constraintGenerater.getObjFunc(netContext,flows);
    }
}
