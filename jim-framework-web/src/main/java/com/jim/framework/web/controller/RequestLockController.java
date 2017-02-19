package com.jim.framework.web.controller;

import com.jim.framework.web.common.ValueResult;
import com.jim.framework.annotationlock.annotation.RequestLockable;
import com.jim.framework.web.service.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

/**
 * 注解式锁的测试
 * Created by jiang on 2016/12/5.
 */
@RestController
@RequestMapping("/lock")
public class RequestLockController extends BaseController {

    @Autowired
    private TestDataService testDataService;

    @RequestLockable(key = {"#id"},expirationTime = 3000)
    @RequestMapping("/{id}")
    public ValueResult<Integer> getById( @PathVariable final int id) throws UnknownHostException {
        return this.returnValueSuccess(Integer.valueOf(this.testDataService.get(id)));

    }


}
