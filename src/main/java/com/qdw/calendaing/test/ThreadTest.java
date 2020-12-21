package com.qdw.calendaing.test;

/**
 * @PackageName:com.qdw.calendaing.test
 * @ClassName: ThreadTest
 * @Description:
 * @date: 2020/12/11 0011 15:58
 */
public class ThreadTest {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable(),"1");
        thread1.start();
        Thread thread2 = new Thread(new MyRunnable(),"2");
        thread2.start();
    }
}

class MyRunnable implements Runnable{

    @Override
    public void run() {
        System.out.println("现在在"+Thread.currentThread().getName()+"中");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
