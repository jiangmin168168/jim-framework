package com.jim.framework.rpc.consumer.service.impl;

import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.consumer.service.ProductCommentMockService;
import org.springframework.stereotype.Service;

/**
 * Created by jiangmin on 2017/12/2.
 */
@Service
public class ProductCommentMockServiceImpl implements ProductCommentMockService {
    @Override
    public Product getById(Long productId) {

        Product mockProduct=new Product();
        mockProduct.setId(0L);
        mockProduct.setName("mock product name");

        return mockProduct;
    }
}
