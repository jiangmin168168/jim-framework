package com.jim.framework.cloud.provider.service.impl;

import com.jim.framework.cloud.provider.model.Product;
import com.jim.framework.cloud.provider.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by jiang on 2017/3/28.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public Product getProductById(Long id) {
        logger.info("getProductById is called");
        Product product=new Product();
        product.setId(id);
        product.setName("apple"+id);
        return product;
    }
}
