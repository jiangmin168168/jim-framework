package com.jim.framework.rpc.server;

import com.jim.framework.rpc.codec.RpcDecoder;
import com.jim.framework.rpc.codec.RpcEncoder;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.common.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiang on 2017/5/10.
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerInitializer.class);
    private final Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        logger.info("RpcServerInitializer.initChannel");
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcServerHandler(this.handlerMap));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("RpcServerInitializer.setApplicationContext");
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (null!=serviceBeanMap) {
            for (Object serviceBean : serviceBeanMap.values()) {
                Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
                for(Class<?> clazz:interfaces) {
                    String interfaceName = clazz.getName();
                    handlerMap.put(interfaceName, serviceBean);
                }
            }
        }
    }
}
