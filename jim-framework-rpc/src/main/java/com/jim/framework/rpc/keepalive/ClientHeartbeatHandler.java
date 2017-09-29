package com.jim.framework.rpc.keepalive;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by jim on 2017/9/27.
 */
public class ClientHeartbeatHandler extends AbstractHeartbeatHandler {

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        this.sendPing(ctx);
    }
}
