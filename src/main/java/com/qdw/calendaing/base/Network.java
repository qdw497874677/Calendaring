package com.qdw.calendaing.base;

import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.TopoConfig;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.constant.TopoStrType;
import javafx.util.Pair;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: Network
 * @Description:
 * @date: 2020/11/8 0008 20:12
 */
@Data
public class Network implements Cloneable {
    private int slotSize;
    private NetTopo netTopo;
    private Map<Integer,Node> nodes = new LinkedHashMap<>();
    private Map<String,Link> links = new LinkedHashMap<>();
    private Map<String,List<Path>> pathCache = new LinkedHashMap<>();
//    private Flow flows = new Flow();
    public Network(int timeSlot){
        this.slotSize = timeSlot;
    }


    public Network initializeNetwork(TopoConfig topoConfig, PathConfig pathConfig, PathProducer pathProducer){
        if (topoConfig==null || StringUtils.isEmpty(topoConfig.getTopoStr())){
            String topoStr =
                    "0-1-10.0," +
                    "0-2-10.0," +
                    "0-3-10.0," +
                    "1-3-10.0," +
                    "2-3-10.0";
            if (topoConfig==null){
                topoConfig = new TopoConfig(topoStr,4,0, TopoStrType.YOURONGLIANG);
            }
        }
        return initializeNetwork2(topoConfig,pathConfig,pathProducer);
    }
    public Network initializeNetwork2(TopoConfig topoConfig,PathConfig pathConfig,PathProducer pathProducer){
        this.netTopo = NetTopo.createTopo(topoConfig);
//        System.out.println("###  "+timeSlot);

        // 初始化节点
        creatNodes(topoConfig.getNumOfNode());
        // 初始化链路
        creatLinks(netTopo.getGraph(),topoConfig);
        // 根据配置初始化默认路径
        creatPaths(netTopo.getGraph(),pathConfig,pathProducer);
        return this;
    }


    private void creatNodes(int nodeSize){
        for (int i = 0; i < nodeSize; i++) {
            nodes.put(i,new Node(i));
        }
    }

    private void creatLinks(double[][] g,TopoConfig topoConfig){
        double value = topoConfig.getCapacity();
        for (int i = 0; i < g.length; i++) {
            for (int j = i; j < g[0].length; j++) {
                if(g[i][j]>0.0){
                    Link link = new Link(nodes.get(i), nodes.get(j));
                    link.initializeInfo(slotSize,value,1);
                    links.put(link.getId(),link);
                }
            }
        }
    }

    private void creatPaths(double[][] g, PathConfig pathConfig, PathProducer pathProducer){
        int maxNum = pathConfig.getMaxNum();
        int maxHop = pathConfig.getMaxHop();
        int numOfNode = g.length;
        List<String> list = new LinkedList<>();
        for (int i = 0; i < numOfNode; i++) {
            for (int j = i+1; j < numOfNode; j++) {
                String pathStr = pathProducer.getPathStr(i, j, numOfNode, netTopo, maxNum, maxHop);
                System.out.println("^^^^^"+i+" "+j+"  "+pathStr);
                if (!StringUtils.isBlank(pathStr)){
                    list.add(pathStr);
                }
            }
        }
        String pathStrs = String.join(",", list);
        addPath(Path.buildPath(this, pathStrs).stream().map(Pair::getKey).collect(Collectors.toList()));
    }

    private void addPath(List<Path> paths){
        if (paths.size()==0){
            return;
        }
        for (Path path : paths) {
            if (pathCache.containsKey(path.getPathId())){
                pathCache.get(path.getPathId()).add(path);
            }else {
                List<Path> list = new ArrayList<>();
                list.add(path);
                pathCache.put(path.getPathId(),list);
            }
        }
    }


    public List<Path> getPath(int sId,int dId){
        if (sId>dId){
            int temp = sId;
            sId = dId;
            dId = temp;
        }
        return pathCache.get(sId+"-"+dId);
    }

    public void updateBandwidth(Path path,int timeSlotLeft,int timeSlotRight,double value){
        for (int i = timeSlotLeft; i <= timeSlotRight; i++) {
            updateBandwidth(path,i,value);
        }
    }

    public void updateBandwidth(Map<Integer,List<Flow>> flowsOfR){
        for (List<Flow> flows : flowsOfR.values()) {
            updateBandwidth(flows);
        }
    }
    public void updateBandwidth(List<Flow> flows){
        for (Flow flow : flows) {
            updateBandwidth(flow.getPath(),flow.getTimeSlot(),flow.getValue());
        }
    }
    public void updateBandwidth(Flow flow){
        if (flow.getStatus().equals(FlowStatus.XUNI)){
            return;
        }
        updateBandwidth(flow.getPath(),flow.getTimeSlot(),flow.getValue());
    }
    // 更新链路带宽
    public void updateBandwidth(Path path,int timeSlot,double value){
//        System.out.println(path);
//        System.out.println(path.getLinksSet().size());
        if (path==null){
            return;
        }
        for (Link link : path.getLinksMap().values()) {
//            System.out.println("!!!!"+value);
//            System.out.println();
            link.decrease(value, timeSlot);
        }
    }

    public String getPathCacheInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("path cache info:\n----------\n");
        for (String s : pathCache.keySet()) {
            sb.append(s).append(":\n");
            List<Path> paths = pathCache.get(s);
            for (Path path : paths) {
                sb.append(path.getPathStr()).append("\n");
            }
            sb.append("---\n");
        }
        sb.append("----------");
        return sb.toString();
    }

    public Node getNode(int id){
        return nodes.get(id);
    }
    public Link getLink(int a,int b){
        if(a>b){
            int temp = a;
            a = b;
            b = temp;
        }
        return links.get(a+"-"+b);
    }

    public String getLinksInfo(){
        StringBuilder sb = new StringBuilder();
        for (Link link : links.values()) {
            sb.append(link).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Network{" +
                "slotSize=" + slotSize +
                ", nodes=" + nodes +
                ", links=" + links +
                ", paths=" + pathCache.size() +
                ", flows=" +  +
                '}';
    }



}
