package com.qdw.calendaing.base;

import com.alibaba.fastjson.JSONObject;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.config.TopoConfig;
import com.qdw.calendaing.base.constant.TopoStrType;
import com.qdw.calendaing.base.pathBase.kpaths.DelMinBLinkKPathsProducer;
import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;
import com.qdw.calendaing.base.pathBase.MaxBandwidthPathProducer;
import com.qdw.calendaing.base.pathBase.PathProducer;
import com.qdw.calendaing.base.requirementBase.RandomRProducer;
import com.qdw.calendaing.base.requirementBase.RequirementProducer;
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

    private static volatile NetContext netContext;

    private Network network;
    int numOfTimeSlot;

    private TopoConfig topoConfig;
    private PathConfig pathConfig;


    private Requirements requirements;

    private RequirementConfig requirementConfig;
    private RequirementProducer requirementProducer;

    // 是否允许多路径传输
    // 对LP无效
    private boolean isMulti;

    // 默认的初始化
    public NetContext(){
        //    String topoStr = "0-1,0-2,1-2,1-3,2-3,2-4,3-5,4-5,5-6,4-6,4-7,5-8,6-8,2-7,6-9,7-9,8-9,7-10,7-11,9-10,10-11";
//    int numOfNode = 12;
        String topoStr = "0-2,0-4,2-4,1-2,1-3,4-3,4-11,1-5,3-5,5-6,5-7,11-10,11-6,6-7,6-8,8-10,10-9,9-12,10-15,8-15,6-16,7-21,21-23,21-24,23-24,23-16,23-20,16-20,20-25,16-13,25-18,18-19,25-17,17-19,14-17,13-17,12-14,12-13,15-13,12-15,19-22,22-18";
        int numOfNode = 26;
        double capacity = 40.0;
        topoConfig = new TopoConfig(topoStr,numOfNode,capacity, TopoStrType.WURONGLIANG);

        int maxNumOfPath = 6;
        int maxHop = 6;
        pathConfig = new PathConfig(maxNumOfPath, maxHop,new MaxBandwidthPathProducer(),new DelMinBLinkKPathsProducer());

        int numOfR = 200;
        int earliestSlot = 0;
        int latestSlot = 19;
        int demandBase = 6;
        requirementConfig = new RequirementConfig(numOfR,earliestSlot,latestSlot,demandBase,requirementProducer);
        numOfTimeSlot = latestSlot;
        requirementProducer = new RandomRProducer();
    }

    public NetContext(String topoStr,int numOfNode,double capacity,
                      PathConfig pathConfig,
                      RequirementConfig requirementConfig){
        topoConfig = new TopoConfig(topoStr,numOfNode,capacity, TopoStrType.WURONGLIANG);
        this.pathConfig = pathConfig;
        this.requirementConfig = requirementConfig;
        this.numOfTimeSlot = requirementConfig.getLatestSlot()+1;
    }
    public NetContext(String topoStr,int numOfNode,double capacity,
                      PathConfig pathConfig,
                      RequirementConfig requirementConfig,
                      List<JSONObject> reqsJson){
        topoConfig = new TopoConfig(topoStr,numOfNode,capacity, TopoStrType.WURONGLIANG);
        this.pathConfig = pathConfig;
        this.requirementConfig = requirementConfig;
        this.requirementProducer = requirementConfig.getRequirementProducer();
        this.numOfTimeSlot = requirementConfig.getLatestSlot()+1;
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
    static public NetContext getNetContext(String topoStr,int numOfNode,double capacity,
                                           PathConfig pathConfig,
                                           RequirementConfig requirementConfig){
        if (netContext==null){
            synchronized (NetContext.class){
                if (netContext==null){
                    netContext = new NetContext(topoStr,numOfNode,capacity,pathConfig,requirementConfig);
                }
            }
        }
        return netContext;
    }



    // 初始上下文
    public void refresh(){
        // 初始化网络
        network = new Network(numOfTimeSlot);
        network.initializeNetwork(topoConfig,pathConfig);
//        network.initializeNetwork(null,pathConfig,pathProducer);
        System.out.println(network.getPathCacheInfo());

        // 初始化请求
        requirements = new Requirements(requirementConfig);
        requirements.initializeRs(network);
    }

    // 根据一个已有的请求集合克隆请求，初始上下文
    public void refresh(Requirements requirements){
        network = new Network(numOfTimeSlot);
        network.initializeNetwork(topoConfig,pathConfig);
        System.out.println(network.getPathCacheInfo());
        this.requirements = requirements.clone();
    }

    // 初始化每个请求的默认流
    public void initializeRequirementsFlows(){
        requirements.initializeRs(network);
        requirements.initializeFlows(pathConfig,network,true);
        System.out.println("initializeRequirementsFlows~~~~~~~~~~");
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }


    @Override
    public String toString() {
        return "NetContext{" +
                "节点对间最大路径个数（LP用）=" + pathConfig.getMaxNum() +
                ", 路径最大跳数=" + pathConfig.getMaxHop() +
//                ", 拓扑信息='" + topoStr + '\'' +
                ", 网络时隙个数=" + numOfTimeSlot +
                ", 网络节点个数=" + topoConfig.getNumOfNode() +
                ", 网络链路个数=" + network.getLinks().size() +
                ", 网络链路初始容量=" + topoConfig.getCapacity() +
                ", 是否支持多路径=" + isMulti +
                ", 路径生成器=" + pathConfig.getPathProducer().getClass().getSimpleName() +
                ", 多路径生成器=" + pathConfig.getKPathsProducer().getClass().getSimpleName()+
                ", 请求个数=" + requirementConfig.getNumOfR() +
                ", 请求最早时隙=" + requirementConfig.getEarliestSlot() +
                ", 请求最晚时隙=" + requirementConfig.getLatestSlot() +
                ", 需求基数=" + requirementConfig.getDemandBase() +
                ", 请求生成器=" + requirementConfig.getRequirementProducer().getClass().getSimpleName() +
                ", 优先级修改器=" + requirementConfig.getPriorityModifier().getClass().getSimpleName() +
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
