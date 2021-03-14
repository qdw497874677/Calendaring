package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

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


    // ��ȡ������Լ��
    public List<List<Integer>> getMBCons(NetContext netContext,Collection<Flow> flows){
        int flowsOfAll = flows.size();
        System.out.println("���г�ʼ��������Ϊ:"+flowsOfAll);

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
//        System.out.println("��������Ϊ��" + res.get(0).size());
        System.out.println("getLLCons size!!!:"+res.size());
        return res;
    }


    Integer getCost(Flow flow,int plusValue,int valueOfXUNI){

        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
            // �������������������Ӧ����������ȼ�Ҳ��������Ȩֵ�ļ���
            return flow.getPath().getPath().size()*plusValue;
        }else {
//            return valueOfXUNI;
            return plusValue * 100;
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
