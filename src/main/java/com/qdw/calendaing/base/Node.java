package com.qdw.calendaing.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @PackageName:com.qdw.calendaing.base
 * @ClassName: Node
 * @Description:
 * @date: 2020/11/8 0008 19:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class NodeInfo{
        int timeSlot;
    }

    private int id;

}
