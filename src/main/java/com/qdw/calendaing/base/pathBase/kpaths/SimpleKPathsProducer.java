package com.qdw.calendaing.base.pathBase.kpaths;

import com.qdw.calendaing.base.NetTopo;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.pathBase.PathProducer;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2020/12/30 0030 15:55
 */
public class SimpleKPathsProducer implements K_PathsProducer {

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
            clone.updateGraph(pathStr,0.0);
        }

        return String.join(",",res);
    }

    private void deleteEdgeByPath(double value,double[][] g){
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
