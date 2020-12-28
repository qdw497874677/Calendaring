package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.ConstraintType;

import java.util.Collection;
import java.util.List;

public interface ConstraintGenerater {

 List<List<Integer>> generate(NetContext netContext, Collection<Flow> flows, int timeSlot, ConstraintType constraintType);

 List<Integer> getObjFunc(NetContext netContext, Collection<Flow> flows,int timeSlot);
}
