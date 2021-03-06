package com.qdw.calendaing.base.pathBase.kpaths;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.TopoConfig;
import com.qdw.calendaing.base.constant.TopoStrType;
import com.qdw.calendaing.base.pathBase.AbstractPathProducer;
import com.qdw.calendaing.base.pathBase.MaxBandwidthPathWithBdwLimitProducer;
import com.qdw.calendaing.base.pathBase.PathProducer;
import com.qdw.calendaing.base.pathBase.ShortestMaxBandwidthPathWithBdwLimitProducer;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;


public class DelMinBLinkKPathsProducer implements K_PathsProducer {

    // 选择k个不重叠的路径
    @Override
    public String getPathsStr(PathProducer pathProducer, int s, int d, int numOfNode, NetTopo netTopo, PathConfig pathConfig,double maxBdw) {

        NetTopo clone = null;
        try {
            clone = netTopo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<String> res = new LinkedList<>();
        int maxNum = pathConfig.getMaxNum();
        while (maxNum-->0){
//            System.out.println(clone);
            String pathStr = pathProducer.getPathStr(s,d,numOfNode,clone,maxBdw);
            if (StringUtils.isBlank(pathStr)){
                break;
            }
            res.add(pathStr);

            // 更新拓扑，进行接下来的寻路
            String minBdwLinkStr = findMinBdwLinkStr(netTopo, pathStr);
            clone.updateGraph(minBdwLinkStr,0.0);
            System.out.println("更新链路"+minBdwLinkStr+"为 0.0");
        }

        return String.join(",",res);
    }

    // 找出路径中最小带宽的链路和带宽
    // 10:1-2
    private String findMinBdwLinkStr(NetTopo netTopo,String pathStr){
        String path = pathStr.split(":")[1];
        String[] nodes = path.split("-");
        double[][] graph = netTopo.getGraph();
        double minBdw = Integer.MAX_VALUE;
        int minA = -1;
        int minB = -1;
        for (int i = 1; i < nodes.length; i++) {
            if (i!=1 && i==nodes.length-1){
                continue;
            }
            int a = Integer.parseInt(nodes[i-1]);
            int b = Integer.parseInt(nodes[i]);
            // 多一个等于的条件，原远离源节点更好（后续也要改进为远离目的节点,一定要改）
            if (graph[a][b] <= minBdw){
                minBdw = graph[a][b];
                minA = a;
                minB = b;
            }
        }
        if (minA!=-1 && minB!=-1){
            System.out.println("!!!!! "+minBdw+":"+minA+"-"+minB);
            return minBdw+":"+minA+"-"+minB;
        }
        return "";
    }

    private void deleteEdgeByB(double value,double[][] g){
        int n = g.length;
        int m = g[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < m; j++) {
                if (g[i][j] < value){
                    g[i][j] = 0;
                    g[j][i] = 0;
                }

            }
        }
    }

    public static void main(String[] args) {
        double[][] g = new double[][]{
                {0.0,10.0,10.0,10.0},
                {10.0,0.0,11.0,0.0},
                {10.0,11.0,0.0,12.0},
                {10.0,0.0,12.0,0.0},
        };

//        String topoStr = "0-1,0-3,0-5,0-6,0-12,1-5,2-3,3-4,3-6,3-8,5-6,6-7,6-10,7-8,8-9,9-15,9-10,10-12,11-12,12-13,12-16,12-17,13-14,13-15,14-15,14-17,9-15,15-17,16-17,16-18,16-20,15-17,16-17,17-18,18-19,18-20";
//        int numOfNode = 21;
//        double capacity = 40.0;

//        String topoStr = "0-1,0-2,1-2";
//        int numOfNode = 3;
//        double capacity = 10.0;

        String topoStr = "0-1:40,0-4:35,0-5:40,1-2:30,2-3:40,3-4:30,3-5:30,4-5:30";
        int numOfNode = 6;
        double capacity = 40.0;

        TopoConfig topoConfig = new TopoConfig(topoStr,numOfNode,capacity, TopoStrType.YOURONGLIANG);
        NetTopo netTopo = NetTopo.createTopo(topoConfig);
//        NetTopo netTopo = new NetTopo(g);
        AbstractPathProducer maxBandwidthPathProducer = new ShortestMaxBandwidthPathWithBdwLimitProducer();
//        AbstractPathProducer maxBandwidthPathProducer = new MaxBandwidthPathWithBdwLimitProducer();
        DelMinBLinkKPathsProducer delMinBLinkKPathsProducer = new DelMinBLinkKPathsProducer();
        PathConfig pathConfig = new PathConfig();
        pathConfig.setMaxNum(3);
        String pathsStr = delMinBLinkKPathsProducer.getPathsStr(maxBandwidthPathProducer, 0, 3, numOfNode, netTopo, pathConfig, 0);
        System.out.println(pathsStr);
    }
}
