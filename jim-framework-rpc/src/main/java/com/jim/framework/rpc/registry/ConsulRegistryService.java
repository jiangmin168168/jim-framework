package com.jim.framework.rpc.registry;

import com.jim.framework.rpc.common.RpcURL;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;

/**
 * Created by jiang on 2017/5/19.
 */
public class ConsulRegistryService extends AbstractConsulService implements RegistryService {

    private final static int CONSUL_CONNECT_PERIOD=1*1000;

    @Override
    public void register(RpcURL url) {
        Consul consul = this.buildConsul(url.getRegistryHost(),url.getRegistryPort());
        AgentClient agent = consul.agentClient();

        ImmutableRegCheck check = ImmutableRegCheck.builder().tcp(url.getHost()+":"+url.getPort()).interval(CONSUL_HEALTH_INTERVAL).build();
        ImmutableRegistration.Builder builder = ImmutableRegistration.builder();
        builder.id(CONSUL_ID).name(CONSUL_NAME).addTags(CONSUL_TAGS).address(url.getHost()).port(url.getPort()).addChecks(check);

        agent.register(builder.build());

    }

    @Override
    public void unregister(RpcURL url) {

    }

}
