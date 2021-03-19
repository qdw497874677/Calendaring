package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
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

    // 获取最大带宽约束
    private List<List<Integer>> getMBCons(NetContext netContext,Collection<Flow> flows){
        int flowsOfAll = flows.size();
        System.out.println("所有初始流的数量为:"+flowsOfAll);

        List<List<Integer>> res = new LinkedList<>();
        int prefix = 0;
        for (Flow flow : flows) {
            List<Integer> list = new ArrayList<>(flowsOfAll+2);
            double maxBdw = flow.getThisR().getMaxBdw();
            if (flow.getStatus().equals(FlowStatus.XUNI) || maxBdw<=0){
                prefix++;
                continue;
            }
            for (int i = 0; i < prefix; i++) {
                list.add(0);
            }
            list.add(1);
            while (list.size()<flowsOfAll){
                list.add(0);
            }

            list.add(1);
            list.add((int)maxBdw);
            prefix++;
            res.add(list);
        }
//        System.out.println("参数个数为：" + res.get(0).size());
        System.out.println("getLLCons size!!!:"+res.size());
        return res;
    }

    @Override
    public List<Integer> getObjFunc(NetContext netContext, Collection<Flow> flows) {
        return constraintGenerater.getObjFunc(netContext,flows);
    }
}
