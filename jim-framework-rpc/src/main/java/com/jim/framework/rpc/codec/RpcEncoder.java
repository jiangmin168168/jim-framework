package com.jim.framework.rpc.codec;

import com.jim.framework.rpc.exception.RpcException;
import com.jim.framework.rpc.protocol.RpcMessage;
import com.jim.framework.rpc.utils.ProtoStuffSerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
编码器
 * Created by jiang on 2017/5/10.
 */
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, RpcMessage in, ByteBuf out) throws Exception {
        if(null==in){
            throw new RpcException("RpcMessage is null");
        }
        if (genericClass.isInstance(in.getMessageBody())) {
            byte[] data = ProtoStuffSerializeUtil.serialize(in.getMessageBody());
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}