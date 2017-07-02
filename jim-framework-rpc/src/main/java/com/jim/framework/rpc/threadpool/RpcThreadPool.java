package com.jim.framework.rpc.threadpool;

import java.util.concurrent.Executor;

/**
 * Created by jim on 2017/7/2/002.
 */
public interface RpcThreadPool {
    Executor getExecutor(int threadSize,int queues);
}
