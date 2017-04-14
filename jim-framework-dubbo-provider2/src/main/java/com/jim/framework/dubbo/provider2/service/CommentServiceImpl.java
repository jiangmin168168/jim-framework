package com.jim.framework.dubbo.provider2.service;

import com.google.common.collect.Lists;
import com.jim.framework.dubbo.core.model.Comment;
import com.jim.framework.dubbo.core.service.CommentService;

import java.util.List;

/*
* 产品评论服务
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/14
* 修改日期:2017/4/14
*/
public class CommentServiceImpl implements CommentService {

    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        List<Comment> comments= Lists.newArrayList();
        int count=10;
        for(int i=0;i<count;i++){
            Comment comment=new Comment();
            comment.setId(Long.valueOf(i));
            comment.setContent(String.valueOf(i));
            comment.setProductId(productId);
            comment.setUserId(productId);
            comments.add(comment);
        }
        return comments;
    }
}
