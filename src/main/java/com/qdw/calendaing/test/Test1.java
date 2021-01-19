package com.qdw.calendaing.test;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2020/12/30 0030 10:42
 */
public class Test1 {
    public static void main(String[] args) {
        String edge = "1-3,1-5,3-5,2-3,2-4,5-4,5-12,2-6,4-6,6-7,6-8,12-11,12-7,7-8,7-9,9-11,11-10,10-13,11-16,9-16,7-17,8-22,22-24,22-25,24-25,24-17,24-21,17-21,21-26,17-14,26-19,19-20,26-18,18-20,15-18,14-18,13-15,13-14,16-14,13-16,20-23,23-19";
        String[] split = edge.split(",");
        List<String> list = new LinkedList<>();
        for (String s : split) {
            String[] split1 = s.split("-");
            String a = (Integer.parseInt(split1[0])-1)+"-"+(Integer.parseInt(split1[1])-1);
            list.add(a);
        }
        String join = String.join(",", list);
        System.out.println(join);
    }
}
