package com.jim.framework.cloud.consumer.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
* 版权所属：东软望海科技有限公司
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/1
* 修改日期:2017/4/1
*/
@FeignClient(value = "JIM-CLOUD-PROVIDER-SERVER",fallback = ProductServiceHystrix.class)
public interface ProductService {
    @RequestMapping(value = "/product/{productId}",method = RequestMethod.GET)
    String getById(@PathVariable("productId") final long productId);

}
