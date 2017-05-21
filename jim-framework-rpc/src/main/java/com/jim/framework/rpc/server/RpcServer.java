package com.jim.framework.rpc.server;

import com.jim.framework.rpc.common.RpcURL;
import com.jim.framework.rpc.config.ServiceConfig;
import com.jim.framework.rpc.exception.RpcException;
import com.jim.framework.rpc.registry.ConsulRegistryService;
import com.jim.framework.rpc.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServer {

    private final static Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private ServiceConfig serviceConfig;
    private RpcServerInitializer rpcServerInitializer;

    public RpcServer(ServiceConfig serviceConfig, RpcServerInitializer rpcServerInitializer){
        this.rpcServerInitializer=rpcServerInitializer;
        this.serviceConfig=serviceConfig;
        this.bind(this.serviceConfig);
    }

    public void bind(ServiceConfig serviceConfig) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this.rpcServerInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
            ;

            try {
                ChannelFuture channelFuture = bootstrap.bind(serviceConfig.getHost(),serviceConfig.getPort()).sync();
                RpcURL url=new RpcURL();
                url.setHost(serviceConfig.getHost());
                url.setPort(serviceConfig.getPort());
                url.setRegistryHost(serviceConfig.getRegistryHost());
                url.setRegistryPort(serviceConfig.getRegistryPort());
                RegistryService registryService=new ConsulRegistryService();
                registryService.register(url);
                channelFuture.channel().closeFuture().sync();


            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
