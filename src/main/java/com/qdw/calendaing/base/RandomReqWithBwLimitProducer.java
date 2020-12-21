package com.qdw.calendaing.base;

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
        int demandBase = requirementConfig.getDemandBase();
        double demand = 0;
        for (int i = 0; i < b - a + 1; i++) {
            demand += random.nextInt(demandBase*2-demandBase/2)+demandBase/2;
        }

        int size = network.getNodes().size();
        int s = random.nextInt(size);
        int d = 0;
        do {
            d = random.nextInt(size);
        }while (d==s);

        return new Requirements.Requirement(ids++,network.getNode(s),network.getNode(d),a,b,demand);
    }

}
