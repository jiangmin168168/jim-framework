package com.jim.framework.rpc.registry;

import com.jim.framework.rpc.common.RpcURL;

/**
 * Created by jiang on 2017/5/19.
 */
public interface RegistryService {
    void register(RpcURL url);
    void unregister(RpcURL url);
}
