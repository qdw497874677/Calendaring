package com.qdw.calendaing.base.config;

import com.qdw.calendaing.base.constant.TopoStrType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @PackageName:com.qdw.calendaing.base.config
 * @ClassName: TopoConfig
 * @Description:
 * @date: 2020/11/30 0030 10:57
 */
@Data
@AllArgsConstructor
public class TopoConfig {
    private String topoStr;
    private int numOfNode;
    private double capacity;
    private TopoStrType topoStrType;
}
