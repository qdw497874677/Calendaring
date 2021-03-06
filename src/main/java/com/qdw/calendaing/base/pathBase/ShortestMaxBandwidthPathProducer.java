package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;

import java.util.Arrays;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: MaxBandwidthPathProducer
 * @Description:
 * @date: 2020/11/8 0008 22:06
 */
public class ShortestMaxBandwidthPathProducer extends AbstractPathProducer {
    /*
     * dj最大带宽，double
     */

    // 返回字符串表示的路径和带宽  带宽:节点A-节点B
    public String getPathByDijkstraMaxbandwidth(int s, int d, int numOfNode, NetTopo topo) {
        if (s < 0 || s >= numOfNode) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] edges = null;
        int[][] edgesOfDistance = null;
        try {
            NetTopo clone = topo.clone();
            edges = clone.getGraph();
            edgesOfDistance = clone.getGraphByDistance();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int[] distance = new int[numOfNode];
        Arrays.fill(distance,Integer.MAX_VALUE);
        double[] bandwidth = new double[numOfNode];// 存放源点到其他点的最大可用带宽（经过链路的最小带宽）
        boolean[] visited = new boolean[numOfNode];
        String[] path = new String[numOfNode];
        for(int i = 0; i < numOfNode; i++) {
            // 初始化源节点s到所有节点的带宽
            bandwidth[i] = edges[s][i];
            // 初始化源节点s到所有节点的距离
            distance[i] = edgesOfDistance[s][i];
            path[i] = s + "-" + i;
        }
//        for (int i = 0; i < numOfNode; i++) {
//            for (int j = 0; j < numOfNode; j++) {
//                if (edges[i][j]==0){
//                    edges[i][j] = Integer.MAX_VALUE;
//                }
//            }
//        }
        visited[s] = true;//将s顶点标记为找到最短路径

        // 处理从源点到其余顶点的最短路径

        for (int i = 0; i < numOfNode; i++) {
            int min = Integer.MAX_VALUE;
            double max = 0;
            //            double max = 0;
            int index = -1;
            // 比较从源点到其余顶点的路径长度、带宽大小
            for (int j = 0; j < numOfNode; j++) {
                // 优先跳数最小，其次带宽最大
                if (!visited[j] && (distance[j] < min || bandwidth[j] > max)) {
                    index = j;//index记录，源节点到j顶点的最短路径
                    min = distance[j];
                    max = bandwidth[j];

                }

            }
            if (index==-1){
                continue;
            }
            distance[index] = min;
            bandwidth[index] = max;
            //找到源点到索引为index顶点的最短路径长度
            visited[index] = true;
            // 更新当前最短路径及距离
            for (int w = 0; w < numOfNode; w++) {
                //最小跳数中的最大带宽
                int temp = distance[w]-edgesOfDistance[index][w];
                if (!visited[w] && (temp > distance[index] || (temp == distance[index] && Math.min(bandwidth[index],edges[index][w]) > bandwidth[w]))) {
                    distance[w] = distance[index] + edgesOfDistance[index][w];
                    bandwidth[w] = Math.min(bandwidth[index],edges[index][w]);
                    path[w] = path[index]+"-"+w;
                }
            }
        }
        if (bandwidth[d]==0.0){
            return "";
        }
        return bandwidth[d]+":"+path[d];
    }




    @Override
    public String getPathStr(int s, int d, int numOfNode, NetTopo netTopo, double maxBdw) {
        return getPathByDijkstraMaxbandwidth(s,d,numOfNode,netTopo);
    }





    public static void main(String[] args) {
        double[][] g = new double[][]{
                {0.0,10.0,10.0,10.0},
                {10.0,0.0,11.0,0.0},
                {10.0,11.0,0.0,12.0},
                {10.0,0.0,12.0,0.0},
        };
        NetTopo netTopo = new NetTopo(g);
        ShortestMaxBandwidthPathProducer maxBandwidthPathProducer = new ShortestMaxBandwidthPathProducer();
        System.out.println(maxBandwidthPathProducer.getPathByDijkstraMaxbandwidth(1, 3, 4, netTopo));
    }






}
