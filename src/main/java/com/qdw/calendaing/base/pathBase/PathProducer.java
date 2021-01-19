package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: PathProducer
 * @Description:
 * @date: 2020/11/8 0008 21:44
 */
public interface PathProducer {

    String getPathStr(int s, int d, int numOfNode, NetTopo netTopo, double maxBdw);
    String getPathsStr(K_PathsProducer k_pathsProducer, int s, int d, int numOfNode, NetTopo netTopo,PathConfig pathConfig, double maxBdw);


}
