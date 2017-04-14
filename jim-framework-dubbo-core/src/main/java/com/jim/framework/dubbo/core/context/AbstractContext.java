package com.jim.framework.dubbo.core.context;

import lombok.Getter;
import lombok.Setter;

/*
* 上下文基类
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public abstract class AbstractContext {

    @Getter
    @Setter
    private String applicationName;


}
