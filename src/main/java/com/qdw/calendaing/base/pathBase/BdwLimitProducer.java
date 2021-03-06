package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;
import org.apache.commons.lang.StringUtils;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/2/4 0004 21:57
 */
public class BdwLimitProducer implements PathProducer {

    AbstractPathProducer pathProducer;

    public BdwLimitProducer(AbstractPathProducer pathProducer){
        this.pathProducer = pathProducer;
    }

    @Override
    public String getPathStr(int s, int d, int numOfNode, NetTopo netTopo, double maxBdw) {
        String pathStr = pathProducer.getPathStr(s, d, numOfNode, netTopo, maxBdw);
        if (!StringUtils.isBlank(pathStr) && maxBdw>0 && Double.parseDouble(pathStr.split(":")[0]) > maxBdw){
            pathStr = maxBdw + ":" + pathStr.split(":")[1];
        }
        return pathStr;
    }

    @Override
    public String getPathsStr(K_PathsProducer k_pathsProducer, int s, int d, int numOfNode, NetTopo netTopo, PathConfig pathConfig, double maxBdw) {
        return pathProducer.getPathsStr(k_pathsProducer,s,d,numOfNode,netTopo,pathConfig,maxBdw);
    }
}
