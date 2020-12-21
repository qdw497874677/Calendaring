package com.qdw.calendaing.base.config;

import lombok.Data;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: PathConfig
 * @Description:
 * @date: 2020/11/8 0008 22:28
 */

@Data
public class PathConfig {
    private int maxNum;
    private int maxHop;

    public PathConfig(int maxNum,int maxHop){
        this.maxNum = maxNum;
        this.maxHop = maxHop;
    }
    public PathConfig(String filePath){

    }
}
