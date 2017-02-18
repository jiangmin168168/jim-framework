package com.jim.service;

import com.jim.dao.generated.entity.Product;

/**
 * Created by jiang on 2016/12/22.
 */
public interface ProductService {
    Product getById(Long id);
    void save(Product product);
}
