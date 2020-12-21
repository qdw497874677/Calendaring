package com.qdw.calendaing.base;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: PathProducer
 * @Description:
 * @date: 2020/11/8 0008 21:44
 */
public interface PathProducer {

    String getPathStr(int s,int d,int numOfNode, NetTopo netTopo, int maxNum, int maxHop);


}
