package com.jim.framework.rpc.beans;

import com.jim.framework.rpc.client.RpcClient;
import com.jim.framework.rpc.client.RpcReference;
import com.jim.framework.rpc.config.ReferenceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 主要是为了支持调用端直接通过注解方式获取远程接口
 * @Reference
 * private ProductService productService;
 * Created by jiang on 2017/5/10.
 */
public class BeanPostPrcessorReference implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BeanPostPrcessorReference.class);

    private ReferenceConfig referenceConfig;

    private RpcClient rpcClient;

    public BeanPostPrcessorReference(ReferenceConfig referenceConfig){
        this.referenceConfig=referenceConfig;
        rpcClient=new RpcClient(this.referenceConfig);
        logger.info("BeanPostPrcessorReference construct end");
    }

    private boolean isProxyBean(Object bean) {
        return AopUtils.isAopProxy(bean);
    }

    public Object initRpcReferenceBean(Object bean, String beanName){
        Class<?> clazz = bean.getClass();
        if(isProxyBean(bean)){
            clazz = AopUtils.getTargetClass(bean);
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set")
                    && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers())
                    && ! Modifier.isStatic(method.getModifiers())) {
                try {
                    RpcReference reference = method.getAnnotation(RpcReference.class);
                    if (reference != null) {
                        Object value = this.rpcClient.createProxy(method.getParameterTypes()[0],reference);
                        if (value != null) {
                            method.invoke(bean, new Object[] { value });
                        }
                    }
                } catch (Exception e) {
                    throw new BeanInitializationException("Failed to init remote service reference at method " + name + " in class " + bean.getClass().getName(), e);
                }
            }
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (! field.isAccessible()) {
                    field.setAccessible(true);
                }
                RpcReference reference = field.getAnnotation(RpcReference.class);
                if (reference != null) {
                    Object value=this.rpcClient.createProxy(field.getType(),reference);
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

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return this.initRpcReferenceBean(bean,beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
