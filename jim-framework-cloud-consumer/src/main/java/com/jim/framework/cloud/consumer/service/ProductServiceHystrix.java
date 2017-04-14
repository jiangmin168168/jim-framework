package com.jim.framework.cloud.consumer.service;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/*
* 产品服务hystrix服务
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/1
* 修改日期:2017/4/1
*/
@Component
public class ProductServiceHystrix implements ProductService {
    @Override
    public String getById(@PathVariable("productId") long productId) {
        return "product is null";
    }
}
