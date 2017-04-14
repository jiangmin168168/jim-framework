package com.jim.framework.dubbo.core.service;

import com.jim.framework.dubbo.core.model.Comment;

import java.util.List;

/*
* 评论服务
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/14
* 修改日期:2017/4/14
*/
public interface CommentService {

    List<Comment> getCommentsByProductId(Long productId);

}
