package com.jim.framework.rpc.consumer.service.impl;

import com.jim.framework.rpc.api.model.Comment;
import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.api.service.CommentService;
import com.jim.framework.rpc.api.service.ProductService;
import com.jim.framework.rpc.client.ResponseCallback;
import com.jim.framework.rpc.client.RpcReference;
import com.jim.framework.rpc.common.RpcResponse;
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

    @RpcReference(isSync = false)
    private CommentService commentService;

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
        //callback
        this.productServiceAsync.getById(productId*2);
        RpcContext.getContext().getResponseFuture().setResponseCallback(new ResponseCallback() {
            @Override
            public void onSuccess(Object response) {
                Product productCallback=(Product) ((RpcResponse)response).getResult();
                System.out.println("product:"+productCallback.getId());
            }

            @Override
            public void onException(RuntimeException ex) {
                System.out.println(ex.getMessage());

            }
        });

        this.commentService.getCommentByProductId(productId*4);
        RpcContext.getContext().getResponseFuture().setResponseCallback(new ResponseCallback() {
            @Override
            public void onSuccess(Object response) {
                Comment commentCallback=(Comment) ((RpcResponse)response).getResult();
                System.out.println("comment:"+commentCallback.getId());
            }

            @Override
            public void onException(RuntimeException ex) {

            }
        });

        //return product;
        return null;
    }
}
