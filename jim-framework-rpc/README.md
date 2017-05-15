# 简单RPC框架-学习使用
基于netty4,protostuff的出于学习目的的RPC框架，后续会完善功能。
> 背景

做微服务有不短时间了，单纯RPC框架呢生产环境上之前主要使用dubbo，出于学习了解过Spring Cloud以及其它的比如Finagle,grpc,thrift。看过dubbo部分源码，了解过RPC的基本原理，但不系统。

> 写一个类似dubbo的有多难


猛的一看dubbo源码的工程构建的话，代码量不少，工程大大小小估计有十几二十个，一时不知从何处下手，如果不反复debug源码的话一时半会想了解清楚还是有一定难度的。如何面对一个庞大的工程呢？我的办法就是化繁为简，先看最为核心的，那么根据RPC的基本原理，首先就只观注如下图的几个部分就可以了：

+ 远程通信,dubbo支持多种远程通信协议，先只看netty
+ 编码，远程通信时需要将信息进行编码以及解码
+ 客户端代理,RPC的主要特点就是将远程调用本地化，实现这一目标的手段就是代理

以上这些，就可以实现一个基本的RPC调用了，但dubbo为什么有那么多功能呢，因为：
+ 支持了强大的SPI机制，让用户方便的扩展原有功能
+ 提供了多个已经实现的扩展功能，比如远程通信除了netty还有mina，注册中心除了推荐的ZK还是redis等
+ 在服务治理上下了很大的功夫，比如服务注册与发现，限流，多线程等

综上所看实现一个简易的RPC并不难，难在对于功能的抽象,扩展点的支持，性能优化等方面上。为了系统的了解这些优秀RPC框架，我想按自己的思路实现以此验证实现一个RPC到底有多难。我并没有完全从零开始设计然后编码，因为我喜欢参考一些开源的项目，因为我相信有我这类想法的人一定不在少数，造轮子玩的事很多人都喜欢。


## 项目参考
主要思路来源于如下两个项目，其中第一个项目是原始版本，第二个版本是另外的维护版本。我按我自己的需求进一步修改成自己想要的结果。
+ http://git.oschina.net/huangyong/rpc 版本1
+ https://github.com/luxiaoxun/NettyRpc 版本2

> 另外主要参考dubbo源码

## 变更
出于我个人的理解做了不同程序的调整以及增强。

### 增加客户端引用服务的注解
这里只是为了简单，只实现通过注解方式获取远程接口的实例。先定义引用注解,isSync属性是为了支持同步接口以及异步接口，默认为同步接口。
```java
public @interface RpcReference {
    boolean isSync() default true;
}
```
客户端引用实例使用：

``` java
@RpcReference
private ProductService productService;

```

原理是通过BeanPostProcessor接口来实现注解字段的注入：

```java
public class BeanPostPrcessorReference implements BeanPostProcessor {

    //...

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //...
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (! field.isAccessible()) {
                    field.setAccessible(true);
                }
                RpcReference reference = field.getAnnotation(RpcReference.class);
                if (reference != null) {
                    Object value=this.rpcClient.createProxy(field.getType(),reference.isSync());
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName() + " in class " + bean.getClass().getName(), e);
            }
        }
        return bean;
    }

}
```

### 增加服务注解的功能
原项目的RpcService需要指定唯一的远程接口，感觉有限制，修改为支持多接口的远程服务。
```java
@RpcService
public class ProductServiceImpl implements ProductService,CommentService 
```

### 重构代码

+ 针对版本1同步调用存在的问题，参考了dubbo的思路，版本1在客户端获取结果时有这么段代码,它的问题在于channelRead0方法的执行有可能出现在obj.wait()方法之前，这样有可能造成客户端永远获取不到预期的结果。

```java
@Override
public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
    this.response = response;

    synchronized (obj) {
        obj.notifyAll();
    }
}

public RpcResponse send(RpcRequest request) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
        //....

        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.channel().writeAndFlush(request).sync();

        synchronized (obj) {
            obj.wait();
        }

        if (response != null) {
            future.channel().closeFuture().sync();
        }
        return response;
    } finally {
        group.shutdownGracefully();
    }
}
```

修改后的版本,核心就是参考dubbo的做法返回一个ResponseFuture，在远程方法回调时更新真实的返回值，最后通过get（）阻塞方法获取结果。由于我对原方案变更比较多就不贴代码了，有兴趣的可以看这：https://github.com/jiangmin168168/jim-framework


+ 针对版本2在同步获取服务端返回结果采用AbstractQueuedSynchronizer，感觉有些复杂，采用Lock替代
ReponseFuture获取结果的逻辑：
```java
private ReentrantLock lock = new ReentrantLock();
private Condition doneCondition=lock.newCondition();
public Object get(long timeout, TimeUnit unit) {
    long start = System.currentTimeMillis();
    if (!this.isDone()) {
        this.lock.lock();
        try{
            while (!this.isDone()) {
                this.doneCondition.await(2000,TimeUnit.MICROSECONDS);
                if(System.currentTimeMillis()-start>timeout){
                    break;
                }
            }
        }
        catch (InterruptedException ex){
            throw new RpcException(ex);
        }
        finally {
            this.lock.unlock();
        }
    }
    return this.getResultFromResponse();
}
```

+ 针对程序关闭时资源的回收，参考了dubbo的思路，采用addShutdownHook注册回收函数
+ 增加了filter机制,这个功能对RPC是非常重要的，比如日志，异常，权限等通用功能的动态植入。
定义filter接口
```java
public interface RpcFilter<T> {
    <T> T invoke(RpcInvoker invoker, RpcInvocation invocation);
}
```

定义filter注解
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ActiveFilter {
    String[] group() default {};
    String[] value() default {};
}

```

server invoker
```java
public class RpcServerInvoker extends AbstractInvoker<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcServerInvoker(Map<String, Object> handlerMap, Map<String,RpcFilter> filterMap) {
        super(handlerMap,filterMap);
        this.handlerMap=handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) {

        //...
    }

    @Override
    public RpcResponse invoke(RpcInvocation invocation) {
        //...
    }

}
```

AbstractInvoker构造函数中的filterMap是通过下面方式注入。


```java
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> implements ApplicationContextAware {

    //...

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> rpcFilterMap = applicationContext.getBeansWithAnnotation(ActiveFilter.class);
        if (null!=rpcFilterMap) {
            for (Object filterBean : rpcFilterMap.values()) {
                Class<?>[] interfaces = filterBean.getClass().getInterfaces();
                ActiveFilter activeFilter=filterBean.getClass().getAnnotation(ActiveFilter.class);
                if(null!=activeFilter.group()&& Arrays.stream(activeFilter.group()).filter(p->p.contains(ConstantConfig.PROVIDER)).count()==0){
                    continue;
                }
                for(Class<?> clazz:interfaces) {
                    if(clazz.isAssignableFrom(RpcFilter.class)){
                        this.filterMap.put(filterBean.getClass().getName(),(RpcFilter) filterBean);
                    }
                }
            }
        }
    }
}
```

AbstractInvoker,主要有ServerInvoker以及ClientInvoker两个子类，两个子类分别获取不同作用域的Filter然后构建Filter执行链。
```java
public abstract class AbstractInvoker<T> extends SimpleChannelInboundHandler<T> implements RpcInvoker {

    private final Map<String, Object> handlerMap;
    private final Map<String,RpcFilter> filterMap;

    protected AbstractInvoker(Map<String, Object> handlerMap, Map<String,RpcFilter> filterMap){
        this.handlerMap = handlerMap;
        this.filterMap=filterMap;
    }

    public RpcInvocation buildRpcInvocation(RpcRequest request){
        //...
    }

    public RpcInvoker buildInvokerChain(final RpcInvoker invoker) {
        RpcInvoker last = invoker;
        List<RpcFilter> filters = Lists.newArrayList(this.filterMap.values());

        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i --) {
                final RpcFilter filter = filters.get(i);
                final RpcInvoker next = last;
                last = new RpcInvoker() {
                    @Override
                    public Object invoke(RpcInvocation invocation) {
                        return filter.invoke(next, invocation);
                    }
                };
            }
        }
        return last;
    }

    protected abstract void channelRead0(ChannelHandlerContext channelHandlerContext, T t);

    public abstract Object invoke(RpcInvocation invocation);

}

```


构建filter链,入口点是在构建代理的逻辑中。
```java

public class RpcProxy <T> implements InvocationHandler {
    //...

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //...

        RpcInvoker rpcInvoker=invoker.buildInvokerChain(invoker);
        ResponseFuture response=(ResponseFuture) rpcInvoker.invoke(invoker.buildRpcInvocation(request));

        if(isSync){
            return response.get();
        }
        else {
            RpcContext.getContext().setResponseFuture(response);
            return null;
        }
    }
}
```

+ 版本2的异步实现有点奇怪，感觉调用方式不RPC(特别是call接口，需要以字符串形式描述调用的方法)

```java
IAsyncObjectProxy client = rpcClient.createAsync(HelloService.class);
RPCFuture helloFuture = client.call("hello", Integer.toString(i));
String result = (String) helloFuture.get(3000, TimeUnit.MILLISECONDS);
```

通过dubbo的方式的版本：创建代理的逻辑中根据是否同步来返回不同的值，如果是同步那么调用阻塞方法获取实时返回的值，如果是异步直接返回null，同时将ResponseFuture放入RpcContext这个上下文变量中。
```java
RpcInvoker rpcInvoker=invoker.buildInvokerChain(invoker);
ResponseFuture response=(ResponseFuture) rpcInvoker.invoke(invoker.buildRpcInvocation(request));

if(isSync){
    return response.get();
}
else {
    RpcContext.getContext().setResponseFuture(response);
    return null;
}
```

获取异步接口：
```java
@RpcReference(isSync = false)
private ProductService productServiceAsync;
```

异步方法调用，获取结果时需要从RpcContext中获取，感觉调用有点复杂，特别是需要通过RpcContext来获取，后续有更好的方案再更新。

```java
Product responseFuture= this.productServiceAsync.getById(productId);
if(null==responseFuture){
    System.out.println("async call result:product is null");
    Product responseFutureResult= (Product) RpcContext.getContext().getResponseFuture().get();
    if(null!=responseFutureResult){
        System.out.println("async call result:"+responseFutureResult.getId());
    }
}
```


+ 调整了目录结构以及类名
根据自己的理解，重命令了一些类名，也调整了一些目录结构。

>至此，RPC拥有了远程通信，序列化，同步异步调用，客户端代理，Filter等常用功能。所依赖的包也有限，要想完善RPC无非是做加法以及优化。尽管不能写也一个超过dubbo的项目，但至少可以用自己的思路去模仿，并不是那么的不可想象。


## 未来添加的功能
+ 服务注册发现
+ 限流/熔断
+ 服务版本
+ 客户端多线程
+ ......


