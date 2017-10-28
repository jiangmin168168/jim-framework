package com.jim.framework.rpc.utils;

import com.google.common.collect.Lists;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.config.ConstantConfig;
import com.jim.framework.rpc.filter.ActiveFilter;

import java.util.*;

/**
 * Created by jim on 2017/10/28.
 */
public class ActiveFilterUtil {

    private static List<Object> getActiveFilter(){
        List<Object> rpcFilterList= Lists.newArrayList();
        Map<String, Object> rpcFilterMapObject = ApplicationContextUtils.getApplicationContext().getBeansWithAnnotation(ActiveFilter.class);
        if (null!=rpcFilterMapObject) {
            rpcFilterList = Lists.newArrayList(rpcFilterMapObject.values());
            Collections.sort(rpcFilterList, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    ActiveFilter activeFilterO1 = o1.getClass().getAnnotation(ActiveFilter.class);
                    ActiveFilter activeFilterO2 = o2.getClass().getAnnotation(ActiveFilter.class);
                    return activeFilterO1.order() > activeFilterO2.order() ? 1 : -1;
                }
            });
        }
        return rpcFilterList;
    }

    public static Map<String,RpcFilter> getFilterMap(boolean isServer){
        List<Object> rpcFilterList=getActiveFilter();
        Map<String,RpcFilter> filterMap=new HashMap<>();
        for (Object filterBean : rpcFilterList) {
            Class<?>[] interfaces = filterBean.getClass().getInterfaces();
            ActiveFilter activeFilter=filterBean.getClass().getAnnotation(ActiveFilter.class);
            String includeFilterGroupName=!isServer?ConstantConfig.CONSUMER:ConstantConfig.PROVIDER;
            if(null!=activeFilter.group()&& Arrays.stream(activeFilter.group()).filter(p->p.contains(includeFilterGroupName)).count()==0){
                continue;
            }
            for(Class<?> clazz:interfaces) {
                if(clazz.isAssignableFrom(RpcFilter.class)){
                    filterMap.put(filterBean.getClass().getName(),(RpcFilter) filterBean);
                }
            }
        }
        return filterMap;
    }
}
