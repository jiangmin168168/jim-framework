package com.jim.framework.rpc.api.service;

import com.jim.framework.rpc.api.model.Comment;

/**
 * Created by jiang on 2017/5/9.
 */
public interface CommentService {
    Comment getCommentByProductId(Long productId);
}
