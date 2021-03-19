package com.qdw.calendaing.base.requirementBase;

import com.qdw.calendaing.base.Network;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.config.RequirementConfig;

public interface RequirementProducer {

    Requirements.Requirement getOneR(RequirementConfig requirementConfig, Network networks);

}
