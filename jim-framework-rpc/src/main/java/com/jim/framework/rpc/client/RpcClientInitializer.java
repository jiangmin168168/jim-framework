package com.jim.framework.rpc.client;

import com.jim.framework.rpc.codec.RpcDecoder;
import com.jim.framework.rpc.codec.RpcEncoder;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.common.RpcResponse;
import com.jim.framework.rpc.config.ConstantConfig;
import com.jim.framework.rpc.filter.ActiveFilter;
import com.jim.framework.rpc.utils.ApplicationContextUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private final static Logger logger = LoggerFactory.getLogger(RpcClientInitializer.class);

    private Map<String, RpcFilter> getFilterMap(){
        logger.info("RpcClientInitializer.setApplicationContext");

        Map<String,RpcFilter> filterMap=new HashMap<>();
        Map<String, Object> rpcFilterMapObject = ApplicationContextUtils.getApplicationContext().getBeansWithAnnotation(ActiveFilter.class);
        if (null!=rpcFilterMapObject) {
            for (Object filterBean : rpcFilterMapObject.values()) {
                Class<?>[] interfaces = filterBean.getClass().getInterfaces();
                ActiveFilter activeFilter=filterBean.getClass().getAnnotation(ActiveFilter.class);
                if(null!=activeFilter.group()&& Arrays.stream(activeFilter.group()).filter(p->p.contains(ConstantConfig.CONSUMER)).count()==0){
                    continue;
                }
                for(Class<?> clazz:interfaces) {
                    if(clazz.isAssignableFrom(RpcFilter.class)){
                        filterMap.put(filterBean.getClass().getName(),(RpcFilter) filterBean);
                    }
                }
            }
        }
        return filterMap;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new RpcClientInvoker(this.getFilterMap()));
    }
}