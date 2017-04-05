package com.jim.framework.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

/**
 * Created by jiang on 2017/3/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadPoolTest {

    @Test
    public void testFixedThreadPool(){
        ExecutorService executorService= Executors.newFixedThreadPool(5);

        int taskCount=6;
        for(int i=0;i<taskCount;i++){
            executorService.execute(new MyThreadPoolThread());
        }

    }

    @Test
    public void testFixedThreadPoolCallable() throws ExecutionException, InterruptedException {
        ExecutorService executorService= Executors.newFixedThreadPool(5);

        int taskCount=6;
        for(int i=0;i<taskCount;i++){
            Future<String> threadId= executorService.submit(new MyCallableThreadPoolThread());

            System.out.println("result:"+threadId.get());

            Integer k=0;
            Double kk=0.0;
        }

    }

    @Test
    public void testCachedThreadPool(){
        ExecutorService executorService= Executors.newCachedThreadPool();

        int taskCount=6;
        for(int i=0;i<taskCount;i++){
            executorService.execute(new MyThreadPoolThread());
        }
    }

    @Test
    public void testScheduledThreadPool(){
        ExecutorService executorService= Executors.newScheduledThreadPool(5);

        int taskCount=6;
        for(int i=0;i<taskCount;i++){
            executorService.execute(new MyThreadPoolThread());
        }
    }

    @Test
    public void testSingleThreadPool(){
        ExecutorService executorService= Executors.newSingleThreadExecutor();

        int taskCount=6;
        for(int i=0;i<taskCount;i++){
            executorService.execute(new MyThreadPoolThread());
        }
    }

}

class MyThreadPoolThread implements Runnable{

    @Override
    public void run() {
        System.out.println("MyThreadPoolThread:"+Thread.currentThread().getId());
    }
}

class MyCallableThreadPoolThread implements Callable<String>{

    @Override
    public String call() throws Exception {
        return "MyCallableThreadPoolThread:"+Thread.currentThread().getId();
    }
}
