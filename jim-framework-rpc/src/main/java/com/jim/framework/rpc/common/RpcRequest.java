package com.jim.framework.rpc.common;


import java.util.Map;

/**
请求对象
 * Created by jiang on 2017/5/10.
 */
public class RpcRequest {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private int maxExecutesCount;
    private Map<String,Object> contextParameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public int getMaxExecutesCount() {
        return maxExecutesCount;
    }

    public void setMaxExecutesCount(int maxExecutesCount) {
        this.maxExecutesCount = maxExecutesCount;
    }

    public Map<String, Object> getContextParameters() {
        return contextParameters;
    }

    public void setContextParameters(Map<String, Object> contextParameters) {
        this.contextParameters = contextParameters;
    }
}
