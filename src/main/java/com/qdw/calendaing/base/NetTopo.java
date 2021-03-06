package com.qdw.calendaing.base;

import com.qdw.calendaing.base.config.TopoConfig;
import com.qdw.calendaing.base.constant.TopoStrType;
import com.qdw.calendaing.base.pathBase.Path;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: NetTopo
 * @Description:
 * @date: 2020/11/16 0016 20:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetTopo implements Cloneable {
    private double[][] graph;

    public static NetTopo createTopo(TopoConfig topoConfig){
        NetTopo netTopo = new NetTopo();
        double[][] g = new double[topoConfig.getNumOfNode()][topoConfig.getNumOfNode()];
        String str = topoConfig.getTopoStr();
        for (String s : str.split(",")) {
            String[] linkAndBw = s.split(":");
            String[] split = linkAndBw[0].split("-");
            int i = Integer.parseInt(split[0]);

            int j = Integer.parseInt(split[1]);
            double c = 0;
            if (topoConfig.getTopoStrType().equals(TopoStrType.WURONGLIANG)){
                c = topoConfig.getCapacity();
            }else {
                c = Double.parseDouble(linkAndBw[1]);
            }

            g[i][j] = g[j][i] = c;
        }
        netTopo.setGraph(g);

        return netTopo;
    }



    // 生成topo专用方法
    public static NetTopo createTopoByTimeSlot(NetContext netContext,int timeSlot){
        NetTopo netTopo = new NetTopo();
        int numOfNode = netContext.getNetwork().getNodes().size();
        double[][] g = new double[numOfNode][numOfNode];
        Map<String, Link> links = netContext.getNetwork().getLinks();
        for (Link link : links.values()) {
            double residualCapacity = link.getLinkInfoMap().get(timeSlot).getResidualCapacity();
            g[link.getNodeA().getId()][link.getNodeB().getId()] = residualCapacity;
            g[link.getNodeB().getId()][link.getNodeA().getId()] = residualCapacity;
        }
        netTopo.setGraph(g);
        return netTopo;
    }

    // 10:1-2
    public void updateGraph(String pathStr,double value){
        String[] split = pathStr.split(":");
        split = split[1].split("-");
        for (int i = 1; i < split.length; i++) {
            int a = Integer.parseInt(split[i-1]);
            int b = Integer.parseInt(split[i]);
            graph[a][b] = value;
            graph[b][a] = value;
        }

//        System.out.println("更新了一条路径:"+pathStr+" -> "+value);
    }

    public void updateGraph(Path path,double value){
        String[] split = path.getPathStr().split("-");
        for (int i = 1; i < split.length; i++) {
            int a = Integer.parseInt(split[i-1]);
            int b = Integer.parseInt(split[i]);
            graph[a][b] = value;
            graph[b][a] = value;
        }
    }

    public void updateGraphToDe(Path path, double value){
        String[] split = path.getPathStr().split("-");
        for (int i = 1; i < split.length; i++) {
            int a = Integer.parseInt(split[i-1]);
            int b = Integer.parseInt(split[i]);
            graph[a][b] -= value;
            graph[b][a] -= value;
        }
    }



    public int[][] getGraphByDistance(){
        int n = graph.length;
        int m = graph[0].length;
        int[][] arr = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (graph[i][j]>0){
                    arr[i][j] = 1;
                }else {
                    arr[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return arr;
    }


    @Override
    public NetTopo clone() throws CloneNotSupportedException {
        int n = graph.length;
        int m = graph[0].length;
        double[][] cloneG = new double[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(graph[i],0,cloneG[i],0,m);
        }
        NetTopo clone = (NetTopo) super.clone();
        clone.setGraph(cloneG);
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] doubles : graph) {
            sb.append(Arrays.toString(doubles)).append("\n");
        }
        return sb.toString();
    }
}
