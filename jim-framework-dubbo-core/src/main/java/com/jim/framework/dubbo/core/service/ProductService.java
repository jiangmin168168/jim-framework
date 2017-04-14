package com.jim.framework.dubbo.core.service;

import com.jim.framework.dubbo.core.model.Product;

/*
* 产品接口
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public interface ProductService {

    Product getByid(Long id);

}
