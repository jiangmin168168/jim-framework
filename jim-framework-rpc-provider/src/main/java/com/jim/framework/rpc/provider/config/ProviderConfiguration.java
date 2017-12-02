package com.jim.framework.rpc.provider.config;

import com.jim.framework.rpc.config.ServiceConfig;
import com.jim.framework.rpc.server.RpcServer;
import com.jim.framework.rpc.server.RpcServerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@ComponentScan(basePackages = {"com.jim.framework.rpc.provider","com.jim.framework.rpc"})
@Configuration
public class ProviderConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(ProviderConfiguration.class);

    @Bean
    @Autowired
    public RpcServer rpcServer(RpcServerInitializer rpcServerInitializer){
        logger.info("begin to start");
        ServiceConfig serviceConfig=new ServiceConfig();
        InetAddress address=null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String host="127.0.0.1";
        if(null!=address){
            host=address.getHostAddress();
        }
        serviceConfig.setHost(host);
        serviceConfig.setPort(9988);
        //serviceConfig.setRegistryHost("192.168.237.128");
        serviceConfig.setRegistryHost("127.0.0.1");
        serviceConfig.setRegistryPort(8500);
        RpcServer rpcServer= new RpcServer(serviceConfig,rpcServerInitializer);
        logger.info("service is started");
        return rpcServer;
    }

    @Bean
    public RpcServerInitializer rpcServerInitializer(){
        return new RpcServerInitializer();
    }



}
