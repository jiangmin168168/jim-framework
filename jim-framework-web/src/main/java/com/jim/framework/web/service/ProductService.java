package com.jim.framework.web.service;

import com.jim.framework.web.dao.generated.entity.Product;

/**
 * Created by jiang on 2016/12/22.
 */
public interface ProductService {
    Product getById(Long id);
    void save(Product product);
}
