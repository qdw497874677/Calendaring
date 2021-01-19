package com.qdw.calendaing.base.pathBase.kpaths;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.PathProducer;
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
}
