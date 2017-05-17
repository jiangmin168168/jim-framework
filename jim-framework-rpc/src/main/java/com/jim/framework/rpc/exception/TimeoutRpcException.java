package com.jim.framework.rpc.exception;

/**
 * Created by jiang on 2017/5/17.
 */
public class TimeoutRpcException extends RpcException {

    public TimeoutRpcException(){
        super("time out exception");
    }

    public TimeoutRpcException(String message){
        super(message);
    }
}
