package com.qdw.calendaing.base.pathBase.kpaths;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.PathProducer;

public interface K_PathsProducer {
    String getPathsStr(PathProducer pathProducer, int s, int d, int numOfNode, NetTopo netTopo, PathConfig pathConfig,double maxBdw);
}
