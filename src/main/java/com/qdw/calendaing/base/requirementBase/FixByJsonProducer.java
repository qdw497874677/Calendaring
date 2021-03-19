package com.qdw.calendaing.base.requirementBase;

import com.alibaba.fastjson.JSONObject;
import com.qdw.calendaing.base.Network;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.config.RequirementConfig;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/14 0014 9:15
 */
public class FixByJsonProducer implements RequirementProducer {

    private int curNum = 0;

    public FixByJsonProducer(){

    }

    @Override
    public Requirements.Requirement getOneR(RequirementConfig requirementConfig, Network networks) {
        List<JSONObject> reqsJson = requirementConfig.getReqsJson();
        if (reqsJson==null || reqsJson.size()==0){
            return null;
        }
        Requirements reqs = Requirements.createReqs(Collections.singletonList(reqsJson.get(curNum++)), requirementConfig, networks);
        return reqs.getRequirements().get(0);
    }
}
