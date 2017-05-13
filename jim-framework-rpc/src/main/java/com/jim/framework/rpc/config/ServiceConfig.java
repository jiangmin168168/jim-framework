package com.jim.framework.rpc.config;

/**
服务端配置
 * Created by jiang on 2017/5/10.
 */
public class ServiceConfig {
    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
