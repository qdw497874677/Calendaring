package com.qdw.calendaing.base;

import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.config.TopoConfig;
import com.qdw.calendaing.base.constant.TopoStrType;
import lombok.Data;

import java.util.List;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: NetContent
 * @Description:
 * @date: 2020/11/8 0008 19:02
 */
@Data
public class NetContext implements Cloneable {

    private NetContext(){}
    private static volatile NetContext netContext;

    private Network network;
    private int numOfTimeSlot = 19;
    private int maxNumOfPath = 6;
    private int maxHop = 6;

    private String topoStr = "0-1,0-2,1-2,1-3,2-3,2-4,3-5,4-5,5-6,4-6,4-7,5-8,6-8,2-7,6-9,7-9,8-9,7-10,7-11,9-10,10-11";
    private int numOfNode = 12;

//    private String topoStr = "0-2,0-4,2-4,1-2,1-3,3-4,4-11,1-5,3-5,5-6,5-7,10-11,7-12,7-8,7-9,9-11,10-11,10-13,11-16,9-16,7-17,8-22,22-24,22-25,24-25,24-17,24-21,17-21,21-26,17-14,26-19,19-20,26-18,18-20,15-18,14-18,13-15,13-14,16-14,13-16,20-23,23-19";
//    private int numOfNode = 26;

    private TopoConfig topoConfig;
    private PathConfig pathConfig;
    private PathProducer pathProducer;

    private Requirements requirements;
    private int numOfR = 100;
    private int earliestSlot = 0;
    private int latestSlot = 19;
    private int demandBase = 6;
    private RequirementConfig requirementConfig;
    private RequirementProducer requirementProducer;

    {
        topoConfig = new TopoConfig(topoStr,numOfNode,20.0, TopoStrType.WURONGLIANG);
        pathConfig = new PathConfig(maxNumOfPath, maxHop);
        requirementConfig = new RequirementConfig(numOfR,earliestSlot,latestSlot,demandBase);
        requirementProducer = new RandomRProducer();
        pathProducer = new MaxBandwidthPathProducer();
    }

    static public NetContext getNetContext(){
        if (netContext==null){
            synchronized (NetContext.class){
                if (netContext==null){
                    netContext = new NetContext();
                }
            }
        }
        return netContext;
    }

    // 初始上下文
    public void refresh(){
        // 初始化网络
        network = new Network(numOfTimeSlot);
        network.initializeNetwork(topoConfig,pathConfig,pathProducer);
//        network.initializeNetwork(null,pathConfig,pathProducer);
        System.out.println(network.getPathCacheInfo());

        // 初始化请求
        requirements = new Requirements(requirementConfig);
        requirements.initializeRs(network,requirementProducer);
    }

    // 根据一个已有的请求集合初始上下文
    public void refresh(Requirements requirements){
        network = new Network(numOfTimeSlot);
        network.initializeNetwork(topoConfig,pathConfig,pathProducer);
        System.out.println(network.getPathCacheInfo());
        this.requirements = requirements.clone();
    }

    // 初始化每个请求的默认流
    public void initializeRequirementsFlows(){
        requirements.initializeRs(network,requirementProducer);
        requirements.initializeFlows(pathConfig,network,true);
        System.out.println("initializeRequirementsFlows~~~~~~~~~~");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();

        return super.clone();
    }


    @Override
    public String toString() {
        return "NetContext{" +
                "network=" + network +
                ", 节点对间最大请求个数=" + maxNumOfPath +
                ", 路径最大跳数=" + maxHop +
                ", 拓扑信息='" + topoStr + '\'' +
                ", 网络时隙个数=" + numOfTimeSlot +
                ", 节点个数=" + numOfNode +
                ", topoConfig=" + topoConfig +
                ", pathConfig=" + pathConfig +
                ", pathProducer=" + pathProducer +
//                ", requirements=" + requirements +
                ", 请求个数=" + numOfR +
                ", 请求最早时隙=" + earliestSlot +
                ", 请求最晚时隙=" + latestSlot +
                ", 需求基数=" + demandBase +
                ", requirementConfig=" + requirementConfig +
                ", requirementProducer=" + requirementProducer +
                '}';
    }

    public static void main(String[] args) {
        NetContext netContext = NetContext.getNetContext();
        netContext.refresh();
        netContext.initializeRequirementsFlows();
        System.out.println(netContext.getRequirements());

        System.out.println("重置上下文");

        netContext.refresh(netContext.getRequirements());

        System.out.println(netContext.getRequirements());


    }

}
