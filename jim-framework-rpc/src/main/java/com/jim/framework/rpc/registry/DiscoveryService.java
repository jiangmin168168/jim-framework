package com.jim.framework.rpc.registry;

import com.jim.framework.rpc.common.RpcURL;

import java.util.List;

/**
 * Created by jiang on 2017/5/19.
 */
public interface DiscoveryService {

    List<RpcURL> getUrls(String registryHost, int registryPort);
}
