package com.qdw.calendaing.schedulerStregys.lp.constraintGenerater;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.constant.ConstraintType;

import java.util.Collection;
import java.util.List;

public interface ConstraintGenerater {

 List<List<Double>> generateAll(NetContext netContext, Collection<Flow> flows);

 List<List<Double>> generateOne(NetContext netContext, Collection<Flow> flows, ConstraintType constraintType);

 List<Double> getObjFunc(NetContext netContext, Collection<Flow> flows);
}
