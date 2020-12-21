package com.qdw.calendaing.test;

/**
 * @PackageName:com.qdw.calendaing.test
 * @ClassName: Main
 * @Description:
 * @date: 2020/12/11 0011 11:02
 */
public class Main {
    public static void main(String[] args) {

        int num = 1;

        System.out.println("num="+num);

        num = add(num,2);

        System.out.println("num="+num);

        for (int i = 0; i < 10; i++) {
            num = add(num,1);
        }

        System.out.println("num="+num);

        num++;

        System.out.println("num="+num);

        num++;

        System.out.println("num="+num);

    }

    static private int add(int num,int i){
        System.out.println("@");
        return num+i;
    }

}
