package com.qdw.calendaing;

import com.alibaba.fastjson.JSONObject;
import com.qdw.calendaing.base.Flow;

import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.pathBase.MaxBandwidthPathWithBdwLimitProducer;
import com.qdw.calendaing.base.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.kpaths.SimpleKPathsProducer;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_PM;
import com.qdw.calendaing.schedulerStregys.*;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.schedulerStregys.lp.LPSimpleOfflineScheduler;
import com.qdw.calendaing.schedulerStregys.lp.LPWithBdwLimitScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: Main
 * @Description:
 * @date: 2020/11/26 0026 15:35
 */
public class Main {
    public static void main(String[] args) {


//        String topoStr = "0-2,0-4,2-4,1-2,1-3,4-3,4-11,1-5,3-5,5-6,5-7,11-10,11-6,6-7,6-8,8-10,10-9,9-12,10-15,8-15,6-16,7-21,21-23,21-24,23-24,23-16,23-20,16-20,20-25,16-13,25-18,18-19,25-17,17-19,14-17,13-17,12-14,12-13,15-13,12-15,19-22,22-18";
//        int numOfNode = 26;
//        double capacity = 40.0;

        // ESNET 57
//        String topoStr = "0-1,1-2,2-3,3-4,5-6,6-7,7-8,8-9,0-9,4-10,10-11,11-12,12-13,7-13,12-14,14-15,15-16,17-18,18-19,19-20,7-20,17-21,21-22,22-23,23-24,24-25,25-26,26-27,19-27,23-28,28-29,29-30,30-31,31-32,32-33,33-34,34-35,35-36,23-36,34-37,37-38,38-39,39-40,40-41,26-41,32-42,42-43,43-44,44-45,45-46,46-47,47-48,48-49,49-50,40-49,40-50,44-51,51-54,54-55,55-56,47-52,52-53,53-56";
//        int numOfNode = 57;
//        double capacity = 40.0;

        // ESNET 21
//        String topoStr = "0-1,0-3,0-5,0-6,0-12,1-5,2-3,3-4,3-6,3-8,5-6,6-7,6-10,7-8,8-9,9-15,9-10,10-12,11-12,12-13,12-16,12-17,13-14,13-15,14-15,14-17,9-15,15-17,16-17,16-18,16-20,15-17,16-17,17-18,18-19,18-20";
//        int numOfNode = 21;
//        double capacity = 40.0;

        // SMALL 3
        String topoStr = "0-1,0-2,1-2";
        int numOfNode = 3;
        double capacity = 10.0;

//        PathConfig pathConfig = new PathConfig(6,10,
//                new MaxBandwidthPathProducer(),
//                new SimpleKPathsProducer());

        PathConfig pathConfig = new PathConfig(6,10,
                new MaxBandwidthPathWithBdwLimitProducer(),
                new SimpleKPathsProducer());

//        RequirementConfig requirementConfig = new RequirementConfig(0,19,getReqsJsonList());
        RequirementConfig requirementConfig = new RequirementConfig(0,19,
                new MaxCS_PM(),
                getReqsJsonListWithBdwLimit());
//        RequirementConfig requirementConfig = new RequirementConfig(
//                6,
//                0,
//                19,
//                6,
//                new RandomRProducer()
//        );


        NetContext netContext = NetContext.getNetContext(
                // 网络链路拓扑
                topoStr,
                // 节点数量
                numOfNode,
                // 链路容量
                capacity,
                pathConfig,
                requirementConfig
        );

        netContext.refresh();


        Scheduler scheduler;
//        scheduler = new VbvpEarliestOfflineScheduler();// 离线、全时隙
//            scheduler = new VbvpStepsOfflineScheduler();// 离线、分时隙
//            scheduler = new VbvpEarliestOnlineScheduler();// 在线、全时隙
//            scheduler = new VbvpStepsOnlineScheduler();// 在线、分时隙
//        scheduler = new LPSimpleOfflineScheduler();// 离线、全时隙、LP
//            scheduler = new LPStepsOfflineScheduler();// 离线、分时隙、LP
//             scheduler = new LPStepsOnlineScheduler();// 在线、分时隙、LP
        scheduler = new LPWithBdwLimitScheduler(new LPSimpleOfflineScheduler());

        CalendaingResult calendaingResult = scheduler.calendaing(netContext);
        String print = getPrint(calendaingResult, netContext);



        // 同请求的第二次实验
        netContext.refresh(netContext.getRequirements());
        // 设置为多路
        netContext.setMulti(true);

        scheduler = new VbvpEarliestOfflineScheduler();// 离线、全时隙
//        scheduler = new VbvpEarliestMultipathOfflineScheduler();// 离线、全时隙、多路径

        CalendaingResult calendaingResult2 = scheduler.calendaing(netContext);
        String print2 = getPrint(calendaingResult2, netContext);

        // 打印第一次实验结果
        System.out.println(print);
        // 打印第二次实验结果
        System.out.println(print2);

    }

    static public List<JSONObject> getReqsJsonList(){
        JSONObject json1 = Requirements.getJson(0, 1, 2, 4, 18, -1);
        JSONObject json2 = Requirements.getJson(1, 2, 1, 8, 80,-1);
        JSONObject json3 = Requirements.getJson(0, 2, 0, 6, 28,-1);
        JSONObject json4 = Requirements.getJson(0, 1, 1, 2, 16,-1);
        JSONObject json5 = Requirements.getJson(1, 2, 0, 4, 25,-1);
        List<JSONObject> list = new LinkedList<>();
        list.add(json1);
        list.add(json2);
        list.add(json3);
        list.add(json4);
        list.add(json5);
        return list;
    }
    static public List<JSONObject> getReqsJsonListWithBdwLimit(){
        JSONObject json1 = Requirements.getJson(0, 1, 2, 4, 18, 8);
        JSONObject json2 = Requirements.getJson(1, 2, 1, 8, 80,9);
        JSONObject json3 = Requirements.getJson(0, 2, 0, 6, 28,7);
        JSONObject json4 = Requirements.getJson(0, 1, 1, 2, 16,8);
        JSONObject json5 = Requirements.getJson(1, 2, 0, 4, 25,9);
        List<JSONObject> list = new LinkedList<>();
        list.add(json1);
        list.add(json2);
        list.add(json3);
        list.add(json4);
        list.add(json5);


        return list.stream().limit(5).collect(Collectors.toList());
    }


    static public String getPrint(CalendaingResult calendaingResult,NetContext netContext){
        StringBuilder res = new StringBuilder();
        res.append(calendaingResult);
        res.append(netContext.getNetwork().getLinksInfo()).append("\n");
        res.append("完成率：").append(calendaingResult.getAcceptRate()).append("\n");
        res.append("数据传输率：").append(calendaingResult.getThroughputRate()).append("\n");
        res.append("总路径数量：").append(netContext.getNetwork().getPathCacheSize()).append("\n");
        res.append("节点数：").append(netContext.getNetwork().getNodes().size()).append("\n");
        res.append("节点间最少路径数：").append(netContext.getNetwork().getMinpathSize()).append(" 节点间最多路径数：").append(netContext.getNetwork().getMaxpathSize()).append("\n");
        res.append("平均节点间的路径数量：").append(netContext.getNetwork().getAvgPathSize()).append("\n");
        res.append("缓存路径：").append(netContext.getNetwork().getPathCache()).append("\n");
        res.append("请求：").append(netContext.getRequirements().toString()).append("\n");
//        res.append("流：").append(netContext.getRequirements().get).append("\n");
        res.append("耗时：").append(calendaingResult.getTotalTime()).append("ms").append("\n");
        res.append("未完成请求的传输率：").append(calendaingResult.getReservedRateButRejected()).append("\n");

        for (Requirements.Requirement requirement : netContext.getRequirements().getRequirements()) {
            if (requirement.getMeetDemand()>requirement.getDemand()){
               res.append("!!!").append(requirement.getMeetDemand()).append("  ").append(requirement.getDemand()).append("\n");
                for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                    for (Flow flow : flows) {
                        res.append(flow).append(" ").append(flow.getValue()).append("\n");
                            if (flow.getStatus().equals(FlowStatus.XUNI)){
                            res.append(" 是虚拟流  ").append("\n");
                        }
                    }
                }
            }
        }
        return res.toString();
    }
}
