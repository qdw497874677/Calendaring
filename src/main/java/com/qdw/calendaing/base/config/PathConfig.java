package com.qdw.calendaing.base.config;

import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;
import com.qdw.calendaing.base.pathBase.PathProducer;
import lombok.Data;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: PathConfig
 * @Description:
 * @date: 2020/11/8 0008 22:28
 */

@Data
public class PathConfig {
    private int maxNum;
    private int maxHop;
    // ����������maxBdw��������ʹ�������maxBdw���������û����maxBdw��������pathConfig��maxBdw
    private double maxBdw;
    private PathProducer pathProducer;
    private K_PathsProducer kPathsProducer;

    public PathConfig(int maxNum,int maxHop,PathProducer pathProducer,K_PathsProducer kPathsProducer){
        this.maxNum = maxNum;
        this.maxHop = maxHop;
        this.pathProducer = pathProducer;
        this.kPathsProducer = kPathsProducer;
    }
    public PathConfig(int maxNum,int maxHop,double maxBdw,PathProducer pathProducer,K_PathsProducer kPathsProducer){
        this.maxNum = maxNum;
        this.maxHop = maxHop;
        this.maxBdw = maxBdw;
        this.pathProducer = pathProducer;
        this.kPathsProducer = kPathsProducer;
    }
    public PathConfig(String filePath){

    }
}
