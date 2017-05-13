package com.jim.framework.rpc.proxy;

import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.common.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture implements Future<Object> {

    private RpcRequest request;
    private RpcResponse response;
    private boolean isCancelledFlag;

    private ReentrantLock lock = new ReentrantLock();
    private Condition doneCondition=lock.newCondition();

    public ResponseFuture(RpcRequest request) {
        this.request = request;
    }

    private Object getResultFromResponse(){
        if(isDone()){
            return this.response.getResult();
        }
        throw new RuntimeException("action is not completed");
    }

    @Override
    public boolean isDone() {
        return this.response!=null;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return this.get(6000,TimeUnit.MICROSECONDS);
    }

    @Override
    public Object get(long timeout, TimeUnit unit) {
        long start = System.currentTimeMillis();
        if (!this.isDone()) {
            this.lock.lock();
            try{
                while (!this.isDone()) {
                    this.doneCondition.await(2000,TimeUnit.MICROSECONDS);
                    if(System.currentTimeMillis()-start>timeout){
                        break;
                    }
                }
            }
            catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }
            finally {
                this.lock.unlock();
            }
        }
        return this.getResultFromResponse();
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelledFlag;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(!mayInterruptIfRunning){
            return false;
        }
        RpcResponse errorResult = new RpcResponse();
        errorResult.setRequestId(this.request.getRequestId());
        errorResult.setResult("request future has been canceled.");
        response = errorResult ;
        this.isCancelledFlag=true;
        return true;
    }

    public void done(RpcResponse reponse) {
        this.lock.lock();
        try{
            this.response = reponse;
            this.doneCondition.signal();

        }
        finally {
            this.lock.unlock();
        }
    }
}
