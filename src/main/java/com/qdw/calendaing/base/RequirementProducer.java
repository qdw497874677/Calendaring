package com.qdw.calendaing.base;

import com.qdw.calendaing.base.config.RequirementConfig;

public interface RequirementProducer {

    Requirements.Requirement getOneR(RequirementConfig requirementConfig, Network networks);

}
