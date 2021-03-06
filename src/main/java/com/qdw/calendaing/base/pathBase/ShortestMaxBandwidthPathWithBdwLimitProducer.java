package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;

import java.util.Arrays;

public class ShortestMaxBandwidthPathWithBdwLimitProducer extends AbstractPathProducer {
    /*
     * dj������double������������
     */

    // �����ַ�����ʾ��·���ʹ���  ����:�ڵ�A-�ڵ�B
    public String getPathByDijkstraMaxbandwidth(int s, int d, int numOfNode, NetTopo topo,double maxBdw) {
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
        double[] bandwidth = new double[numOfNode];// ���Դ�㵽������������ô���������·����С����
        boolean[] visited = new boolean[numOfNode];
        String[] path = new String[numOfNode];
        for(int i = 0; i < numOfNode; i++) {
            // ��ʼ��Դ�ڵ�s�����нڵ�Ĵ���
            bandwidth[i] = edges[s][i];
            // ��ʼ��Դ�ڵ�s�����нڵ�ľ���
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
        visited[s] = true;//��s������Ϊ�ҵ����·��

        // �����Դ�㵽���ඥ������·��

        for (int i = 0; i < numOfNode; i++) {
            int min = Integer.MAX_VALUE;
            double max = 0;
    //            double max = 0;
            int index = -1;
            // �Ƚϴ�Դ�㵽���ඥ���·�����ȡ������С
            for (int j = 0; j < numOfNode; j++) {
                // ����������С����δ������
                if (!visited[j] && (distance[j] < min ||(distance[j]==min && bandwidth[j] > max) )) {
                    index = j;//index��¼��Դ�ڵ㵽j��������·��
                    min = distance[j];
                    max = bandwidth[j];

                }

            }
            if (index==-1){
                continue;
            }
            distance[index] = min;
            bandwidth[index] = max;
            //�ҵ�Դ�㵽����Ϊindex��������·������
            visited[index] = true;
            // ���µ�ǰ���·��������
            for (int w = 0; w < numOfNode; w++) {
                //��С�����е�������
                int temp = distance[w]-edgesOfDistance[index][w];
                if (!visited[w] && (temp > distance[index] || (temp == distance[index] && Math.min(bandwidth[index],edges[index][w]) > bandwidth[w]))) {
//                if (!visited[w] && (distance[index]-edgesOfDistance[index][w] > distance[w] || Math.min(bandwidth[index],edges[index][w]) > bandwidth[w])) {
                    distance[w] = distance[index] + edgesOfDistance[index][w];
                    bandwidth[w] = Math.min(bandwidth[index],edges[index][w]);
                    path[w] = path[index]+"-"+w;
                }
            }
        }
//        System.out.println();
        if (bandwidth[d]==0.0){
            return "";
        }
        if (maxBdw>0 && bandwidth[d] > maxBdw){
            bandwidth[d] = maxBdw;
        }
        return bandwidth[d]+":"+path[d];
    }




    @Override
    public String getPathStr(int s, int d, int numOfNode, NetTopo netTopo, double maxBdw) {
        return getPathByDijkstraMaxbandwidth(s,d,numOfNode,netTopo,maxBdw);
    }





    public static void main(String[] args) {
        double[][] g = new double[][]{
                {0.0,10.0,10.0,10.0},
                {10.0,0.0,11.0,0.0},
                {10.0,11.0,0.0,12.0},
                {10.0,0.0,12.0,0.0},
        };
        NetTopo netTopo = new NetTopo(g);
        ShortestMaxBandwidthPathWithBdwLimitProducer maxBandwidthPathProducer = new ShortestMaxBandwidthPathWithBdwLimitProducer();
        String pathByDijkstraMaxbandwidth = maxBandwidthPathProducer.getPathByDijkstraMaxbandwidth(1, 3, 4, netTopo, 10);
        System.out.println();
    }






}
