package com.qdw.calendaing.schedulerStregys.lp;

import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.constant.ConstraintType;

import java.util.List;

public interface ConstraintGenerater {

 List<List<Integer>> generate(NetContext netContext, ConstraintType constraintType);

 List<Integer> getObjFunc(NetContext netContext);
}
