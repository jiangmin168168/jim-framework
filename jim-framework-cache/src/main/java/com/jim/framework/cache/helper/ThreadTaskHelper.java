package com.jim.framework.cache.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jiang on 2017/3/6.
 */
public class ThreadTaskHelper {

    private static ExecutorService executorService= Executors.newFixedThreadPool(20);

    public static void run(Runnable runnable){
        executorService.execute(runnable);
    }
}
