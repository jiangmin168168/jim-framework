package com.jim.framework.rpc.provider.service.impl;

import com.jim.framework.rpc.api.model.Comment;
import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.api.service.CommentService;
import com.jim.framework.rpc.server.RpcService;
import com.jim.framework.rpc.api.service.ProductService;

@RpcService
public class ProductServiceImpl implements ProductService,CommentService {
    @Override
    public Product getById(Long id) {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Product product=new Product();
        product.setId(id);
        product.setName(id+"name");
        return product;
    }

    @Override
    public Comment getCommentByProductId(Long productId) {
        Comment comment=new Comment();
        comment.setProductId(productId);
        comment.setId(productId);
        comment.setContent(productId+"comment");
        return comment;
    }
}
