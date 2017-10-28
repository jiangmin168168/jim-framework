package com.jim.framework.rpc.server;

import com.jim.framework.rpc.codec.RpcDecoder;
import com.jim.framework.rpc.codec.RpcEncoder;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.common.RpcResponse;
import com.jim.framework.rpc.config.ConstantConfig;
import com.jim.framework.rpc.constants.Constants;
import com.jim.framework.rpc.keepalive.ServerHeartbeatHandler;
import com.jim.framework.rpc.threadpool.RpcThreadPoolFactory;
import com.jim.framework.rpc.utils.ActiveFilterUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by jiang on 2017/5/10.
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerInitializer.class);
    private final Map<String, Object> handlerMap = new HashMap<String, Object>();
    private final Map<String,RpcFilter> filterMap=new HashMap<>();

    @Autowired
    private RpcThreadPoolFactory rpcThreadPoolFactory;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        //logger.info("RpcServerInitializer.initChannel");
        ChannelPipeline pipeline = socketChannel.pipeline();
       ;
        Executor executor= this.rpcThreadPoolFactory.getThreadPool(ConstantConfig.DEFAULT_THREAD_POOL_NAME).getExecutor(1,1);
        pipeline
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new IdleStateHandler(Constants.READER_TIME_SECONDS, 0, 0))
                .addLast(new ServerHeartbeatHandler())
                .addLast(new RpcServerInvoker(this.handlerMap,this.filterMap,executor))
        ;
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
        this.filterMap.putAll(ActiveFilterUtil.getFilterMap(true));
    }
}
