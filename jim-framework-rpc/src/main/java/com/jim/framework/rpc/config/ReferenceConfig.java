package com.jim.framework.rpc.config;

/**
消费端配置
 * Created by jiang on 2017/5/10.
 */
public class ReferenceConfig {
    private String host;
    private int port;
    protected long connectTimeoutMillis = 6000;

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

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }
}
