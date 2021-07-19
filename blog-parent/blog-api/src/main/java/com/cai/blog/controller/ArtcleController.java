package com.cai.blog.controller;

import com.cai.blog.common.aop.LogAnnotation;
import com.cai.blog.service.ArticleService;
import com.cai.blog.vo.Params.ArticleParam;
import com.cai.blog.vo.Params.PageParams;
import com.cai.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArtcleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "s首页",operation = "文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){

        return articleService.listArticle(pageParams) ;
    }

    /**
     * 首页  最热文章
     */
    @PostMapping("hot")
    public Result hotArticle(){
        int limit=5;

        return articleService.hotArticle(limit) ;
    }

    /**
     * 首页  最新文章
     */
    @PostMapping("new")
    public Result newArticles(){
        int limit=5;
        return articleService.newArticle(limit);
    }

    /**
     * 文章归档
     */
    @PostMapping("listArchives")
    public Result listArchives(){

        return articleService.listArchives();
    }

    /**
     * 文章详情
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

    /**
     * 发布文章
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

}
