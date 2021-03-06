package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/18 0018 9:22
 */
public abstract class AbstractPathProducer implements PathProducer {

    // ��ȡ�����ڵ�֮���k������·��
    // Ĭ�Ͼ����ò����еĶ�·��������ʹ����ϵ�·���������ɵ�
    @Override
    public String getPathsStr(K_PathsProducer kPathsProducer, int s, int d, int numOfNode, NetTopo netTopo, PathConfig pathConfig, double maxBdw) {
        if (kPathsProducer == null){
            return getPathStr(s,d,numOfNode,netTopo,maxBdw);
        }
        return kPathsProducer.getPathsStr(this,s,d,numOfNode,netTopo,pathConfig,maxBdw);
    }
}
