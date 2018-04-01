package com.jim.framework.activemq.model;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangmin on 2018/1/5.
 */
public class Product {

    public static final ConcurrentHashMap<String,List<String>> transportResult=new ConcurrentHashMap<>();

    public static final List<String> consumerMessageResult= Lists.newArrayList();

}
