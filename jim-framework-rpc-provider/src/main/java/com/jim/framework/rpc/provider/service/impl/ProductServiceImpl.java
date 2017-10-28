package com.jim.framework.rpc.provider.service.impl;

import com.jim.framework.rpc.api.model.Comment;
import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.api.service.CommentService;
import com.jim.framework.rpc.context.RpcContext;
import com.jim.framework.rpc.server.RpcService;
import com.jim.framework.rpc.api.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService
public class ProductServiceImpl implements ProductService,CommentService {

    Logger logger= LoggerFactory.getLogger(ProductServiceImpl.class);

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
        logger.info("get context parameter from server,rpc-version={}",String.valueOf(RpcContext.getContext().getContextParameter("rpc-version")));
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
