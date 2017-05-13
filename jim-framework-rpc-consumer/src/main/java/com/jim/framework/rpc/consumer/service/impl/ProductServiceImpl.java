package com.jim.framework.rpc.consumer.service.impl;

import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.api.service.ProductService;
import com.jim.framework.rpc.client.RpcReference;
import com.jim.framework.rpc.consumer.service.ProductCommentService;
import org.springframework.stereotype.Service;

/**
 * Created by jiang on 2017/5/10.
 */
@Service
public class ProductServiceImpl implements ProductCommentService {

    @RpcReference
    private ProductService productService;

    public Product getById(Long productId){
        return this.productService.getById(productId);
    }
}
