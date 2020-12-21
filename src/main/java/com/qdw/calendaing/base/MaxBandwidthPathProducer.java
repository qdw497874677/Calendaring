package com.qdw.calendaing.base;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: MaxBandwidthPathProducer
 * @Description:
 * @date: 2020/11/8 0008 22:06
 */
public class MaxBandwidthPathProducer implements PathProducer {
    /*
     * dj��������double
     */


    public String getPathByDijkstraMaxbandwidth(int s, int d, int numOfNode, NetTopo topo) {
        if (s < 0 || s >= numOfNode) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] edges = null;
        try {
            edges = topo.clone().getGraph();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        double[] bandwidth = new double[numOfNode];// ���Դ�㵽������ľ���
        boolean[] visited = new boolean[numOfNode];
        String[] path = new String[numOfNode];
        for(int i = 0; i < numOfNode; i++){
            bandwidth[i] = edges[s][i];
            path[i] = s + "-" + i;
        }
//        for (int i = 0; i < numOfNode; i++) {
//            for (int j = 0; j < numOfNode; j++) {
//                if (edges[i][j]==0){
//                    edges[i][j] = Integer.MAX_VALUE;
//                }
//            }
//        }
        visited[s] = true;//��s������Ϊ�ҵ����·��

        // ������Դ�㵽���ඥ������·��

        for (int i = 0; i < numOfNode; i++) {
            double max = 0;
    //            double max = 0;
            int index = -1;
            // �Ƚϴ�Դ�㵽���ඥ���·������
            for (int j = 0; j < numOfNode; j++) {
                // ��Դ�㵽j��������·����û���ҵ�
                // ��Դ�㵽j�����·��������С
                if (!visited[j] && bandwidth[j] > max) {
                    index = j;//index��¼��Դ�ڵ㵽j��������·��
                    max = bandwidth[j];
                }
            }
            if (index==-1){
                continue;
            }
            bandwidth[index] = max;
            //�ҵ�Դ�㵽����Ϊindex��������·������
            visited[index] = true;
            // ���µ�ǰ���·��������
            for (int w = 0; w < numOfNode; w++) {
                if (!visited[w] && Math.min(bandwidth[index],edges[index][w]) > bandwidth[w]) {
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

    @Override
    public String getPathStr(int s,int d,int numOfNode, NetTopo netTopo, int maxNum, int maxHop) {
        NetTopo clone = null;
        try {
            clone = netTopo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<String> res = new LinkedList<>();

        while (maxNum-->0){
//            System.out.println(clone);
            String pathStr = getPathByDijkstraMaxbandwidth(s, d, numOfNode, clone);

            if (StringUtils.isBlank(pathStr)){
                break;
            }
            res.add(pathStr);
            clone.updateGraph(pathStr,0.0);
        }

        return String.join(",",res);

    }

    public static void main(String[] args) {
        double[][] g = new double[][]{
                {0.0,10.0,10.0,10.0},
                {10.0,0.0,11.0,0.0},
                {10.0,11.0,0.0,12.0},
                {10.0,0.0,12.0,0.0},
        };
        NetTopo netTopo = new NetTopo(g);
        MaxBandwidthPathProducer maxBandwidthPathProducer = new MaxBandwidthPathProducer();
        System.out.println(maxBandwidthPathProducer.getPathByDijkstraMaxbandwidth(1, 3, 4, netTopo));
    }






}