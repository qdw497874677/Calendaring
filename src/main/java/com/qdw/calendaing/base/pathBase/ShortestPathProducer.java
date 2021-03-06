package com.qdw.calendaing.base.pathBase;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.kpaths.K_PathsProducer;
import com.qdw.calendaing.base.pathBase.kpaths.SimpleKPathsProducer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: MaxBandwidthPathProducer
 * @Description:
 * @date: 2020/11/8 0008 22:06
 */
public class ShortestPathProducer extends AbstractPathProducer {
    /*
     * ���·��
     */
    public String getPathByDijkstra(int s, int d, int numOfNode, NetTopo topo) {
        if (s < 0 || s >= numOfNode) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int[][] edges = new int[0][];
        try {
            edges = topo.clone().getGraphByDistance();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int[] distance = new int[numOfNode];// ���Դ�㵽������ľ���
        boolean[] visited = new boolean[numOfNode];
        String[] path = new String[numOfNode];
        for(int i = 0; i < numOfNode; i++){
            // ��ʼ��Դ�ڵ�s�����нڵ�ľ���
            distance[i] = edges[s][i]>0?1:0;
            path[i] = new String((s) + "-" + (i));
        }
        for (int i = 0; i < numOfNode; i++) {
            for (int j = 0; j < numOfNode; j++) {
                if (edges[i][j]==0){
                    edges[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        visited[s] = true;//��s������Ϊ�ҵ����·��

        // �����Դ�㵽���ඥ������·��

        for (int i = 0; i < numOfNode; i++) {
            int min = Integer.MAX_VALUE;
//            double max = 0;
            int index = -1;
            // �Ƚϴ�Դ�㵽���ඥ���·������
            for (int j = 0; j < numOfNode; j++) {
                // ��Դ�㵽j��������·����û���ҵ�
                // ��Դ�㵽j�����·��������С
                if (!visited[j] && distance[j] < min) {
                    index = j;//index��¼��Դ�ڵ㵽j��������·��
                    min = distance[j];
                }
            }
            if (index==-1){
                continue;
            }
            distance[index] = min;
            //�ҵ�Դ�㵽����Ϊindex��������·������
            visited[index] = true;
            // ���µ�ǰ���·��������
            for (int w = 0; w < numOfNode; w++) {
                if (!visited[w] && distance[w]-edges[index][w] > distance[index]) {
                    distance[w] = distance[index] + edges[index][w];
                    path[w] = path[index]+"-"+w;
                }
            }


        }
        if (distance[d]==Integer.MAX_VALUE){
            return "";
        }
        return distance[d]+":"+path[d];
    }


    public static void main(String[] args) {
        double[][] g = new double[][]{
                {0.0,10.0,10.0,10.0},
                {10.0,0.0,10.0,0.0},
                {10.0,10.0,0.0,10.0},
                {10.0,0.0,10.0,0.0},
        };
        NetTopo netTopo = new NetTopo(g);
        ShortestPathProducer maxBandwidthPathProducer = new ShortestPathProducer();
//        System.out.println(maxBandwidthPathProducer.getPathByDijkstraMaxbandwidth(1, 3, 4, g));

//        String pathStr = maxBandwidthPathProducer.getPathsStr(new SimpleKPathsProducer(),0, 2, 4, netTopo, 3, 4,new PathConfig());
//        System.out.println(pathStr);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class PathInfo{
        private Integer[] pathArr;
        private Double value;

        public boolean isContain(List<PathInfo> list){
            Integer[] arr;
            for (PathInfo info : list) {
                arr = info.getPathArr();
                if (arr.length!=pathArr.length){
                    return false;
                }
                for (int i = 0; i < pathArr.length; i++) {
                    if (!arr[i].equals(pathArr[i])){
                        return false;
                    }
                }
            }
            return true;
        }
    }
    private PathInfo pathStr2Info(String pathStr){
        PathInfo pathInfo = new PathInfo();
        String[] split = pathStr.split(":");
        pathInfo.setValue(Double.parseDouble(split[0]));
        split = split[0].split("-");
        int n = split.length;
        Integer[] pathArr = new Integer[n];
        for (int i = 0; i < n; i++) {
            pathArr[i] = Integer.parseInt(split[i]);
        }
        pathInfo.setPathArr(pathArr);
        return pathInfo;
    }


    @Override
    public String getPathStr(int s, int d, int numOfNode, NetTopo netTopo, double maxBdw) {
        return getPathByDijkstra(s,d,numOfNode,netTopo);
    }


//    @Override
//    public String getPathStr(int s,int d,int numOfNode, NetTopo netTopo, int maxNum, int maxHop) {
//
//        double[][] g = netTopo.getGraph();
////		int edgescopy[][] = edges.clone();//���һ���ڽӾ���ĸ���
////		int A[][] = new int[k][numOfVexs];
//        List<PathInfo> realPaths = new ArrayList<>();//�洢ȷ����k�����·��
//        List<PathInfo> tempPaths = new ArrayList<>();//�洢Ԥ�������·��
//
//
//
//        realPaths.add(pathStr2Info(getPathBydijkstraMaxbandwidth(s, d, numOfNode, g)));//ԭ
//        //		System.out.print("��һ��·����");
//        //		printList(root_path);//�ȴ�ӡ��һ��·��
////		realPaths.add(PathInt.toPI(root_path));
//
//        for(int i=0;i<k-1;i++) {
//            Double edgescopy[][] = cloneMatrix(g);//���һ���ڽӾ���ĸ���
//
//            Integer[] iterationPath = realPaths.get(i).getPathArr();//��i��·����Ϊ����·��,�����i+1��·�� //ԭ
////			Integer[] iterationPath = PathInt.toI(realPaths.get(i));//��i��·����Ϊ����·��,�����i+1��·��
//
//            System.out.println();
//            System.out.print("���ε���·��·����");
//            printList(iterationPath);//��ӡ����·��
//            for(int j=1;j<iterationPath.length;j++) {//��B���������·������ӵ���·����·����ƫ��·��������ظ�������
//                List<Integer> tempPath = new ArrayList<>();
//                int cost=0;//��ǰ�����ƫ��·���ĳɱ�
//                for(int l=1;l<=j;l++) {//ƫ��ǰ�Ľڵ���빹��ƫ��·����l�ڵ㵽l-1�ڵ����·����ɾ��
//                    if(l-2 >= 0) {
//                        tempPath.add(iterationPath[l-2]);
//                    }
//
//                    if(l != j) {
//                        cost += g[iterationPath[l-1]][iterationPath[l]];//��û�б�Ĳ���·����cost����
//                    }
//                }
//                double tempC = edgescopy[iterationPath[j-1]][iterationPath[j]];
//                edgescopy[iterationPath[j-1]][iterationPath[j]] = 0.0;//ɾ��·��
//                edgescopy[iterationPath[j]][iterationPath[j-1]] = 0.0;//ɾ��·��
////				System.out.println("ɾ����"+iterationPath[j-1]+"-"+iterationPath[j]);
////				dijkstra_maxbandwidth(iterationPath[j], d, numOfVexs, edgescopy);
//                PathInfo tempPathInfo = pathStr2Info(getPathBydijkstraMaxbandwidth(iterationPath[j - 1], d, numOfNode, edgescopy));
//                Integer[] temp = tempPathInfo.getPathArr();
//
//                temp[temp.length-1] = -Math.min(-temp[temp.length-1], -iterationPath[iterationPath.length-1]);
//                boolean haveloop = false;
//                for(Integer eB:tempPath) {//�����л�
//                    for(int e:temp) {
//                        if(eB.equals(e)) {
//                            haveloop = true;
//                            break;
//                        }
//                    }
//
//                }
//
//                System.out.println("	�Ƿ��л���"+haveloop+" �Ƿ���paths_A�������"+tempPath.isisContain(realPaths, tempPath)+" �Ƿ���paths_B�������"+isContain(tempPaths, tempPath)+" path_B�Ĵ���Ϊ��"+(-tempPath.get(tempPath.size()-1)));
//                if(!haveloop && !isContain(realPaths, tempPath)&& !isContain(tempPaths, tempPath) && tempPath.get(tempPath.size()-1)!=0) { //ԭ
//                    tempPaths.add(tempPathInfo);//��ƫ��·�����뼯��B
//                    System.out.print("<��>�����ƫ��·����");
//                }
//
//
//
//                System.out.println("��ԭ�ģ�"+tempC);
////				edgescopy[iterationPath[j-1]][iterationPath[j]] = tempC;//��ԭ·��
////				edgescopy[iterationPath[j]][iterationPath[j-1]] = tempC;//��ԭ·��
//            }
//            int maxB = 0;
//            Integer[] minB_path = null;
//            if(tempPaths.size() == 0) {
//                System.out.println("��·��");
//                break;
//            }
//            //��ƫ��·��������ѡ������·��
//
//            for(Integer[] e:tempPaths) {
//                if(e.length > maxB) {
//                    maxB = e.length;
//                    minB_path = e;
//                }
//            }
//            if(minB_path != null) {
//
//                System.out.print("���ε���ѡ����·����");
////				System.out.println("ȡ������ѡ����·��ǰ��paths_B��Ԫ��������"+tempPaths.size());
//
//                printList(minB_path);
//                realPaths.add(minB_path); //ԭ
//
////				realPaths.add(PathInt.toPI(minB_path));
//                tempPaths.remove(minB_path);
////				System.out.println("ȡ������ѡ����·����paths_B��Ԫ��������"+tempPaths.size());
////				for(Integer[] e:tempPaths) {
////					printList(e);
////				}
//            }
//
//
//        }
//
//        StringBuffer sb = new StringBuffer();
//        System.out.println("���");
//        for(Integer[] e:realPaths) {
//            printList(e);
//            for (int i = 0; i < e.length; i++) {
//                if(i == e.length-1) {
//                    sb.append(",");
//                }else if(i == e.length-2){
//                    sb.append(e[i]);
//                }else {
//                    sb.append(e[i]+"-");
//                }
//
//            }
//        }
//
//
//        return sb.toString();
//    }




}
