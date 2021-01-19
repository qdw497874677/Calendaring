package com.qdw.calendaing.base.requirementBase;

import com.qdw.calendaing.base.Network;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.requirementBase.RequirementProducer;

import java.util.Random;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: RandomRProducer
 * @Description:
 * @date: 2020/11/14 0014 9:26
 */
public class RandomRProducer implements RequirementProducer {
    private int ids = 1;
    @Override
    public Requirements.Requirement getOneR(RequirementConfig requirementConfig, Network network) {

        int l = requirementConfig.getEarliestSlot();
        int r = requirementConfig.getLatestSlot();

        Random random = new Random();
        int a = random.nextInt(r-l)+l;
        int b = random.nextInt(r-l)+l;
        if (a>b){
            int temp = b;
            b = a;
            a = temp;
        }
        int demandBase = requirementConfig.getDemandBase();
        double demand = random.nextInt(demandBase*2-demandBase/2)+demandBase/2;
        demand *= b-a+1;

        int size = network.getNodes().size();
        int s = random.nextInt(size);
        int d = 0;
        do {
            d = random.nextInt(size);
        }while (d==s);

        return new Requirements.Requirement(ids++,network.getNode(s),network.getNode(d),a,b,demand);
    }

}
