package com.jim.framework.rpc.exception;

/**
 * Created by jiang on 2017/5/14.
 */
public class RpcException extends RuntimeException {
    public RpcException(String errorMsg){
        super(errorMsg);
    }
    public RpcException(Exception ex){
        super(ex);
    }
    public RpcException(){
        super("rpc exception");
    }
}
