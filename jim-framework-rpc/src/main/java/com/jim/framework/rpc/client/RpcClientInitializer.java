package com.jim.framework.rpc.client;

import com.jim.framework.rpc.codec.RpcDecoder;
import com.jim.framework.rpc.codec.RpcEncoder;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.common.RpcResponse;
import com.jim.framework.rpc.constants.Constants;
import com.jim.framework.rpc.keepalive.ClientHeartbeatHandler;
import com.jim.framework.rpc.utils.ActiveFilterUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private final static Logger logger = LoggerFactory.getLogger(RpcClientInitializer.class);

    private Map<String, RpcFilter> getFilterMap(){
        logger.info("RpcClientInitializer.setApplicationContext");

        return ActiveFilterUtil.getFilterMap(false);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();

        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new IdleStateHandler(0, 0, Constants.ALLIDLE_TIME_SECONDS));
        cp.addLast(new ClientHeartbeatHandler());
        cp.addLast(new RpcClientInvoker(this.getFilterMap()));

    }
}