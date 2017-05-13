package com.jim.framework.rpc.consumer.service;

import com.jim.framework.rpc.api.model.Product;

/**
 * Created by jiang on 2017/5/10.
 */
public interface ProductCommentService {
    Product getById(Long productId);
}
