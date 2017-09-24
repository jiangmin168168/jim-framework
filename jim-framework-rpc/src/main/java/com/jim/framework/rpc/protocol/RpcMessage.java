package com.jim.framework.rpc.protocol;

import java.io.Serializable;

/**
 * Created by jim on 2017/9/24.
 */
public class RpcMessage implements Serializable {

    private RpcMessageHeader messageHeader;

    private Object messageBody;

    public RpcMessageHeader getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(RpcMessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }
}
