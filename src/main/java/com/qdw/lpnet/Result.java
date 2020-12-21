package com.qdw.lpnet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @PackageName:com.qdw.lpnet
 * @ClassName: Result
 * @Description:
 * @date: 2020/11/8 0008 18:18
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Map<Requirement,List<Integer>> map;




}
