package com.jim.framework.rpc.registry;

import com.google.common.collect.Lists;
import com.jim.framework.rpc.cache.RpcClientInvokerCache;
import com.jim.framework.rpc.common.RpcURL;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ImmutableServiceHealth;
import com.orbitz.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jiang on 2017/5/21.
 */
public class ConsulDiscoveryService extends AbstractConsulService implements DiscoveryService {

    private final static Logger logger = LoggerFactory.getLogger(ConsulDiscoveryService.class);

    @Override
    public List<RpcURL> getUrls(String registryHost,int registryPort) {
        List<RpcURL> urls= Lists.newArrayList();
        Consul consul = this.buildConsul(registryHost,registryPort);
        HealthClient client = consul.healthClient();
        String name = CONSUL_NAME;
        ConsulResponse object= client.getAllServiceInstances(name);
        List<ImmutableServiceHealth> serviceHealths=(List<ImmutableServiceHealth>)object.getResponse();
        for(ImmutableServiceHealth serviceHealth:serviceHealths){
            RpcURL url=new RpcURL();
            url.setHost(serviceHealth.getService().getAddress());
            url.setPort(serviceHealth.getService().getPort());
            urls.add(url);
        }

        try {
            ServiceHealthCache serviceHealthCache = ServiceHealthCache.newCache(client, name);
            serviceHealthCache.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>() {
                @Override
                public void notify(Map<ServiceHealthKey, ServiceHealth> map) {
                    logger.info("serviceHealthCache.addListener notify");
                    RpcClientInvokerCache.clear();

                }
            });
            serviceHealthCache.start();
        } catch (Exception e) {
            logger.info("serviceHealthCache.start error:",e);
        }
        return urls;
    }
}
