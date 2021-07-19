package com.cai.blog.service;

import com.cai.blog.vo.Params.CommentParam;
import com.cai.blog.vo.Result;

public interface CommentsService {

    /**
     * 根据文章id查询所有的评论列表
     * @param id
     * @return
     */

   Result commentsByArticleId(Long id);

    Result comment(CommentParam commentParam);
}
