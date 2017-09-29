package com.jim.framework.rpc.keepalive;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by jim on 2017/9/27.
 */
public class ServerHeartbeatHandler extends AbstractHeartbeatHandler {

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        logger.info("ServerHeartbeatHandler.handleReaderIdle reader timeout ,close channel");
        ctx.close();
    }

}
