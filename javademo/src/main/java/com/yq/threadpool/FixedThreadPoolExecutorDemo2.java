package com.yq.threadpool;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedThreadPoolExecutorDemo2 {
    public static void main(String[] args) {
       // ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            MyTask task = new MyTask("Task " + i);
            if (i == 2) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //线程数
            int activeCount = executor.getActiveCount();
            long taskCount = executor.getTaskCount();
            BlockingQueue queue = executor.getQueue();
            log.info("A new task has been added {}, activeCount={}, taskCount={}, queue={}",
                    task.getName(), activeCount, taskCount, queue.size());
            executor.execute(task);
        }
        System.out.println("Maximum threads inside pool " + executor.getMaximumPoolSize());
        executor.shutdown();
    }
}
