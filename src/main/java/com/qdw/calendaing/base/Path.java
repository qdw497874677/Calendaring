package com.qdw.calendaing.base;

import javafx.util.Pair;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: Paths
 * @Description:
 * @date: 2020/11/8 0008 19:47
 */
@Data
public class Path {

    private int id;
    private String pathId;
    private List<Node> path;
    private Node src;
    private Node dst;
    private String pathStr;
    private int hopCount;
    private Map<Integer, Flow> map = new HashMap<>();
    private Set<Link> linksSet = new HashSet<>();
    private Map<Integer,Double> residualCapacity = new HashMap<>();
//    // 表示一个时隙路径的容量状态，true或者null表示已经为最新容量，不需要计算
//    private Map<Integer,Boolean> residualCStatu;
    public Path(int id, List<Node> path, String pathStr){
        this.id = id;
        this.path = path;
        this.pathStr = pathStr;
        this.src = path.get(0);
        this.dst = path.get(path.size()-1);
        this.pathId = src.getId()+"-"+dst.getId();
    }

    public Node getFirst(){
        return path.get(0);
    }

    public Node getLast(){
        return path.get(path.size()-1);
    }


    public double getResidualCapacity(int timeSlot){
        // 如果没有设置链路则直接返回-1
        if (linksSet.isEmpty()){
            return -1;
        }
        double min = Integer.MAX_VALUE;
        for (Link link : linksSet) {
            min = Math.min(min,link.getLinkInfoMap().get(timeSlot).getCapacity());
        }
        residualCapacity.put(timeSlot,min);
        return min;
    }

    public boolean isCover(Link link){
        return linksSet.contains(link);
    }
    static private int ids = 1;

    // 1-2-3:10.0,1-2-3-4:10.0
    static public List<Pair<Path,Double>> buildPath(Network network, String pathStr){
        List<Pair<Path,Double>> paths = new ArrayList<>();
        String[] split = pathStr.split(",");
        for (String s : split) {
//            System.out.println(s);
            if (StringUtils.isBlank(s)){
                continue;
            }
            String[] pathOne = s.split(":");
//            double value = Double.parseDouble(pathOne[0]);
            List<Node> pathNodes = new LinkedList<>();
            String[] path = pathOne[1].split("-");
            boolean flag = false;
            for (String node : path) {
                Node getNode = network.getNode(Integer.parseInt(node));
                if (getNode!=null){
                    pathNodes.add(getNode);
                }else {
                    flag = true;
                    break;
                }
            }
            if (pathNodes.size()<2 || flag){
                System.out.println("创建路径失败  "+pathOne[1]);
                continue;
            }
            Path newPath = new Path(ids, pathNodes, pathOne[1]);
            newPath.addLinks(network);
            paths.add(new Pair<>(newPath,Double.parseDouble(pathOne[0])));
            ids++;
        }
        return paths;
    }

    private void addLink(Link link){
        linksSet.add(link);
    }

    private void addLinks(Network network){
        if (linksSet.size()>0){
            return;
        }

        // 把路径中的链路添加到set中
        for (int i = 1; i < path.size(); i++) {
            Link link = network.getLink(path.get(i - 1).getId(), path.get(i).getId());
            addLink(link);
//            System.out.println(path.get(i - 1).getId()+" "+path.get(i).getId());
//            System.out.println(link);
            link.addPath(this);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(src, path.src) &&
                Objects.equals(dst, path.dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst);
    }

    @Override
    public String toString() {
        return "Path{" +
                "pathStr=" + pathStr +
                '}';
    }
}
