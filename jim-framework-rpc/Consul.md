# 简单RPC框架-基于Consul的服务注册与发现
一般我们常见的RPC框架都包含如下三个部分：
+ 注册中心，用于服务端注册远程服务以及客户端发现服务
+ 服务端，对外提供后台服务，将自己的服务信息注册到注册中心
+ 客户端，从注册中心获取远程服务的注册信息，然后进行远程过程调用
上面提到的注册中心其实属于服务治理，即使没有注册中心，RPC的功能也是完整的。之前我大多接触的是基于zookeeper的注册中心，这里基于consul来实现注册中心的基本功能。

## Consul的一些特点：
+ Raft相比Paxos直接

> 此外不多描述,还没研究raft

+ 支持数据中心，可以用来解决单点故障之类的问题
+ 集成相比zookeeper更加简单
+ 支持健康检查,支持http以及tcp
+ 自带UI管理功能，不需要额外第三方支持

>启动consul之后访问管理页面


## RPC集成
提取出服务注册与服务发现两个接口，然后使用Consul实现，这里主要通过consul-client来实现（也可以是consul-api），需要在pom中引入:
```xml
<dependency>
	<groupId>com.orbitz.consul</groupId>
	<artifactId>consul-client</artifactId>
	<version>0.14.1</version>
</dependency>
```
### 服务注册
+ RegistryService
提供服务的注册与删除功能
```java

public interface RegistryService {
    void register(RpcURL url);
    void unregister(RpcURL url);
}
```

+ AbstractConsulService
consul的基类，用于构建Consl对象，服务于服务端以及客户端。
```java

public class AbstractConsulService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConsulService.class);

    protected final static String CONSUL_NAME="consul_node_jim";
    protected final static String CONSUL_ID="consul_node_id";
    protected final static String CONSUL_TAGS="v3";
    protected final static String CONSUL_HEALTH_INTERVAL="1s";

    protected Consul buildConsul(String registryHost, int registryPort){
        return Consul.builder().withHostAndPort(HostAndPort.fromString(registryHost+":"+registryPort)).build();
    }
}
```

+ ConsulRegistryService
服务注册实现类，在注册服务的同时，指定了健康检查。
> 服务的删除暂时未实现

```java

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
```

由于我实现的RPC是基于TCP的，所以服务注册的健康检查也指定为TCP，consul会按指定的IP以及端口建立连接以此判断服务的健康状态。如果是http，则需要调用http方法，同时指定健康检查地址。
```java
ImmutableRegCheck check = ImmutableRegCheck.builder().tcp(url.getHost()+":"+url.getPort()).interval(CONSUL_HEALTH_INTERVAL).build();
```
后台的监控信息如下：


> 虽然只是指定了TCP,可以出去某种机制后台依然会发起HTTP的健康检查请求



### 服务发现
+ DiscoveryService
获取所有注册的有效的服务信息。
```java
public interface DiscoveryService {

    List<RpcURL> getUrls(String registryHost, int registryPort);
}
```

+ ConsulDiscoveryService
首先是获取有效的服务列表：
```java

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
```
服务更新监听，当可用服务列表发现变化时需要通知调用端。
```java

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
```

由于之前对客户端的Invoker有缓存，所以当服务列表有变化时需要对缓存信息进行更新。
> 这里简单的直接对缓存做清除处理，其实好一点的方法应该只对有变化的做处理。

+ RpcClientInvokerCache
对客户端实例化后的Invoker的缓存类
```java

public class RpcClientInvokerCache {

    private static CopyOnWriteArrayList<RpcClientInvoker> connectedHandlers = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlersClone(){
        return (CopyOnWriteArrayList<RpcClientInvoker>) RpcClientInvokerCache.getConnectedHandlers().clone();
    }

    public static void addHandler(RpcClientInvoker handler) {
        CopyOnWriteArrayList<RpcClientInvoker> newHandlers = getConnectedHandlersClone();
        newHandlers.add(handler);
        connectedHandlers=newHandlers;
    }

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlers(){
        return connectedHandlers;
    }

    public static RpcClientInvoker get(int i){
        return connectedHandlers.get(i);
    }

    public static int size(){
        return connectedHandlers.size();
    }

    public static void clear(){
        CopyOnWriteArrayList<RpcClientInvoker> newHandlers = getConnectedHandlersClone();
        newHandlers.clear();
        connectedHandlers=newHandlers;
    }
}
```

+ 负载均衡
当同一个接口有多个服务同时提供服务时，客户端需要有一定的负载均衡机制去决策将客户端的请求分配给哪一台服务器，这里实现一个简易的轮询实现方式。请求次数累加，累加的值与服务列表的大小做取模操作。

> 代码中取服务列表的方法有小问题，未按接口信息取，后续再完成

```java

public class RoundRobinLoadbalanceService implements LoadbalanceService {

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private static final int MAX_VALUE=1000;
    private static final int MIN_VALUE=1;

    private AtomicInteger getRoundRobinValue(){
        if(this.roundRobin.getAndAdd(1)>MAX_VALUE){
            this.roundRobin.set(MIN_VALUE);
        }
        return this.roundRobin;
    }

    @Override
    public int index(int size) {
        return  (this.getRoundRobinValue().get() + size) % size;
    }
}
```

### 等完善的功能
+ 代码中取服务列表的方法有小问题，未按接口信息取
+ 注册中心的可用服务地址信息变化时，需要优化为按需更新
+ 注册中心的服务删除未实现

### 源码地址
https://github.com/jiangmin168168/jim-framework




