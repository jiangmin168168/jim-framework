package com.jim.framework.web.service.impl;

import com.jim.framework.web.service.TestDataService;
import org.springframework.stereotype.Service;

/**
 * Created by jiang on 2016/12/22.
 */
@Service
public class TestDataServiceImpl implements TestDataService {
    @Override
    public int get(int i) {
        return i;
    }
}
