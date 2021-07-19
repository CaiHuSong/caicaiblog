package com.cai.blog.controller;

import com.cai.blog.service.CommentsService;
import com.cai.blog.vo.Params.CommentParam;
import com.cai.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;


    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long id){

        return commentsService.commentsByArticleId(id);

    }

    @PostMapping("create/change")
    public Result comment(@RequestBody CommentParam commentParam){

        return commentsService.comment(commentParam);
    }
}
