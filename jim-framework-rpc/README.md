# jim-rpc
基于netty4,protostuff的出于学习目的的RPC框架，后续会完善功能。

## 项目参考
主要思路来源于如下两个项目，其中第一个项目是原始版本，第二个版本是另外的维护版本。我按我自己的需求进一步修改成自己想要的结果。
+ http://git.oschina.net/huangyong/rpc 版本1
+ https://github.com/luxiaoxun/NettyRpc 版本2

> 另外主要参考dubbo源码

## 变更
### 重构代码

+ 针对版本1同步调用存在的问题，参考了dubbo的思路
+ 针对版本2采用AbstractQueuedSynchronizer，感觉有些复杂，采用Lock替代
+ 针对程序关闭时资源的回收，参考了dubbo的思路，采用addShutdownHook注册回收函数
+ 增加了filter机制
+ 调整了目录结构


### 增加客户端引用服务的注解

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

## 未来添加的功能
+ 服务注册发现
+ 限流/熔断
+ 服务版本
+ 客户端多线程
+ 过滤器
+ ......


