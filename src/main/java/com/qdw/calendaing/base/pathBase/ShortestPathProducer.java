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
     * 最短路径
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
        int[] distance = new int[numOfNode];// 存放源点到其他点的矩离
        boolean[] visited = new boolean[numOfNode];
        String[] path = new String[numOfNode];
        for(int i = 0; i < numOfNode; i++){
            // 初始化源节点s到所有节点的距离
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
        visited[s] = true;//将s顶点标记为找到最短路径

        // 处理从源点到其余顶点的最短路径

        for (int i = 0; i < numOfNode; i++) {
            int min = Integer.MAX_VALUE;
//            double max = 0;
            int index = -1;
            // 比较从源点到其余顶点的路径长度
            for (int j = 0; j < numOfNode; j++) {
                // 从源点到j顶点的最短路径还没有找到
                // 从源点到j顶点的路径长度最小
                if (!visited[j] && distance[j] < min) {
                    index = j;//index记录，源节点到j顶点的最短路径
                    min = distance[j];
                }
            }
            if (index==-1){
                continue;
            }
            distance[index] = min;
            //找到源点到索引为index顶点的最短路径长度
            visited[index] = true;
            // 更新当前最短路径及距离
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
////		int edgescopy[][] = edges.clone();//获得一个邻接矩阵的复制
////		int A[][] = new int[k][numOfVexs];
//        List<PathInfo> realPaths = new ArrayList<>();//存储确定的k条最短路径
//        List<PathInfo> tempPaths = new ArrayList<>();//存储预备的最短路径
//
//
//
//        realPaths.add(pathStr2Info(getPathBydijkstraMaxbandwidth(s, d, numOfNode, g)));//原
//        //		System.out.print("第一条路径：");
//        //		printList(root_path);//先打印第一条路径
////		realPaths.add(PathInt.toPI(root_path));
//
//        for(int i=0;i<k-1;i++) {
//            Double edgescopy[][] = cloneMatrix(g);//获得一个邻接矩阵的复制
//
//            Integer[] iterationPath = realPaths.get(i).getPathArr();//第i个路径作为迭代路径,求出第i+1条路径 //原
////			Integer[] iterationPath = PathInt.toI(realPaths.get(i));//第i个路径作为迭代路径,求出第i+1条路径
//
//            System.out.println();
//            System.out.print("本次迭代路径路径：");
//            printList(iterationPath);//打印迭代路径
//            for(int j=1;j<iterationPath.length;j++) {//向B集合中添加路径，添加迭代路径链路数的偏离路径，如果重复不加入
//                List<Integer> tempPath = new ArrayList<>();
//                int cost=0;//当前构造的偏离路径的成本
//                for(int l=1;l<=j;l++) {//偏离前的节点加入构造偏离路径，l节点到l-1节点的链路将被删除
//                    if(l-2 >= 0) {
//                        tempPath.add(iterationPath[l-2]);
//                    }
//
//                    if(l != j) {
//                        cost += g[iterationPath[l-1]][iterationPath[l]];//把没有变的部分路径的cost加上
//                    }
//                }
//                double tempC = edgescopy[iterationPath[j-1]][iterationPath[j]];
//                edgescopy[iterationPath[j-1]][iterationPath[j]] = 0.0;//删除路径
//                edgescopy[iterationPath[j]][iterationPath[j-1]] = 0.0;//删除路径
////				System.out.println("删除了"+iterationPath[j-1]+"-"+iterationPath[j]);
////				dijkstra_maxbandwidth(iterationPath[j], d, numOfVexs, edgescopy);
//                PathInfo tempPathInfo = pathStr2Info(getPathBydijkstraMaxbandwidth(iterationPath[j - 1], d, numOfNode, edgescopy));
//                Integer[] temp = tempPathInfo.getPathArr();
//
//                temp[temp.length-1] = -Math.min(-temp[temp.length-1], -iterationPath[iterationPath.length-1]);
//                boolean haveloop = false;
//                for(Integer eB:tempPath) {//不能有环
//                    for(int e:temp) {
//                        if(eB.equals(e)) {
//                            haveloop = true;
//                            break;
//                        }
//                    }
//
//                }
//
//                System.out.println("	是否有环："+haveloop+" 是否在paths_A里包含："+tempPath.isisContain(realPaths, tempPath)+" 是否在paths_B里包含："+isContain(tempPaths, tempPath)+" path_B的带宽为："+(-tempPath.get(tempPath.size()-1)));
//                if(!haveloop && !isContain(realPaths, tempPath)&& !isContain(tempPaths, tempPath) && tempPath.get(tempPath.size()-1)!=0) { //原
//                    tempPaths.add(tempPathInfo);//将偏离路径加入集合B
//                    System.out.print("<新>加入的偏离路径：");
//                }
//
//
//
//                System.out.println("还原的："+tempC);
////				edgescopy[iterationPath[j-1]][iterationPath[j]] = tempC;//还原路径
////				edgescopy[iterationPath[j]][iterationPath[j-1]] = tempC;//还原路径
//            }
//            int maxB = 0;
//            Integer[] minB_path = null;
//            if(tempPaths.size() == 0) {
//                System.out.println("无路径");
//                break;
//            }
//            //从偏离路径集合中选出最大的路径
//
//            for(Integer[] e:tempPaths) {
//                if(e.length > maxB) {
//                    maxB = e.length;
//                    minB_path = e;
//                }
//            }
//            if(minB_path != null) {
//
//                System.out.print("本次迭代选出的路径：");
////				System.out.println("取出本次选出的路径前，paths_B中元素数量："+tempPaths.size());
//
//                printList(minB_path);
//                realPaths.add(minB_path); //原
//
////				realPaths.add(PathInt.toPI(minB_path));
//                tempPaths.remove(minB_path);
////				System.out.println("取出本次选出的路径后，paths_B中元素数量："+tempPaths.size());
////				for(Integer[] e:tempPaths) {
////					printList(e);
////				}
//            }
//
//
//        }
//
//        StringBuffer sb = new StringBuffer();
//        System.out.println("结果");
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
