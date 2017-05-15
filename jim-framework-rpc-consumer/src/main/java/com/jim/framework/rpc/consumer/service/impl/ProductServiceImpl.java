package com.jim.framework.rpc.consumer.service.impl;

import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.api.service.ProductService;
import com.jim.framework.rpc.client.RpcReference;
import com.jim.framework.rpc.consumer.service.ProductCommentService;
import com.jim.framework.rpc.context.RpcContext;
import org.springframework.stereotype.Service;

/**
 * Created by jiang on 2017/5/10.
 */
@Service
public class ProductServiceImpl implements ProductCommentService {

    @RpcReference
    private ProductService productService;

    @RpcReference(isSync = false)
    private ProductService productServiceAsync;

    public Product getById(Long productId){
        Product product= this.productService.getById(productId);
        Product responseFuture= this.productServiceAsync.getById(productId);
        if(null==responseFuture){
            System.out.println("async call result:product is null");
            Product responseFutureResult= (Product) RpcContext.getContext().getResponseFuture().get();
            if(null!=responseFutureResult){
                System.out.println("async call result:"+responseFutureResult.getId());
            }
        }

        return product;
    }
}
