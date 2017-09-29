package com.jim.framework.rpc.codec;

import com.jim.framework.rpc.protocol.RpcMessage;
import com.jim.framework.rpc.utils.ProtoStuffSerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
解码器
 * Created by jiang on 2017/5/10.
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder {

    private Class<?> genericClass;

    private static final int HEADER_SIZE = 4;

    private static final int LENGTH_FIELD_OFFSET=0;

    private static final int LENGTH_FIELD_LENGTH=4;

    private static final int MAX_FRAME_LENGTH=Integer.MAX_VALUE;

    public RpcDecoder(Class<?> genericClass) {
        super(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH);
        this.genericClass = genericClass;
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame=(ByteBuf)super.decode(ctx,in);
        if(null==frame){
            return null;
        }
        byte[] data = new byte[frame.readInt()];
        frame.readBytes(data);

        RpcMessage message = ProtoStuffSerializeUtil.deserialize(data, RpcMessage.class);
        return message;
    }
}
