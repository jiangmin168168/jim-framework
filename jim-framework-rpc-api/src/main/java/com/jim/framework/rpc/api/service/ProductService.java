package com.jim.framework.rpc.api.service;

import com.jim.framework.rpc.api.model.Product;

public interface ProductService {
    Product getById(Long id);
}
