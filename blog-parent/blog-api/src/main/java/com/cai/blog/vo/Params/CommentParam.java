package com.cai.blog.vo.Params;

import lombok.Data;

//评论参数对象
@Data
public class CommentParam {

    private Long articleId;

    private String content;

    private Long parent;

    private Long toUserId;
}
