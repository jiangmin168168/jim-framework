# 简单RPC框架-学习使用
基于netty4,protostuff,consul的出于学习目的的RPC框架，后续会完善功能。


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


### 增加服务注解的功能
原项目的RpcService需要指定唯一的远程接口，感觉有限制，修改为支持多接口的远程服务。
```java
@RpcService
public class ProductServiceImpl implements ProductService,CommentService 
```

### 基于consul的服务注册与发现

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

>至此，RPC拥有了远程通信，注册中心，序列化，同步异步调用，客户端代理，Filter等常用功能。所依赖的包也有限，要想完善RPC无非是做加法以及优化。尽管不能写也一个超过dubbo的项目，但至少可以用自己的思路去模仿，并不是那么的不可想象。

+ 重新实现了业务线程池

## 未来添加的功能
+ 限流/熔断
+ 服务版本
+ ......


