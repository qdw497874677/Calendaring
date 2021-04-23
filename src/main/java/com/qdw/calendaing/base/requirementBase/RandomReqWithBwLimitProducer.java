package com.qdw.calendaing.base.requirementBase;

import com.qdw.calendaing.base.Network;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.config.RequirementConfig;

import java.util.Random;


public class RandomReqWithBwLimitProducer implements RequirementProducer {
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

//        int a = random.nextInt(r+1-l);
//        int b = random.nextInt(r+1-a)+a;

        int demandBase = requirementConfig.getDemandBase();
        double demand = 0;
        for (int i = 0; i < b - a + 1; i++) {
            demand += random.nextInt(demandBase)+demandBase;
        }

        int size = network.getNodes().size();
        int s = random.nextInt(size);
        int d = 0;
        do {
            d = random.nextInt(size);
        }while (d==s);

        // 限制的最大带宽
        int maxBdw = 0;
        maxBdw = random.nextInt(demandBase) + 1;
        demand = 0;
        for (int i = a; i <=b ; i++) {
            demand += 1+random.nextInt(maxBdw);
        }
//        demand = 2* maxBdw * (random.nextInt(b-a+1)+1);

        return new Requirements.Requirement(ids++,network.getNode(s),network.getNode(d),a,b,demand,maxBdw);
    }

}
