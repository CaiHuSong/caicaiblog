package com.cai.blog.service;

import com.cai.blog.vo.Params.ArticleParam;
import com.cai.blog.vo.Params.PageParams;
import com.cai.blog.vo.Result;
import org.springframework.stereotype.Service;


public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
     Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */

    Result hotArticle(int limit);

    /**
     * 最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);


    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 文章详情
     * @param id
     * @return
     */
    Result findArticleById(Long id);

    Result publish(ArticleParam articleParam);

    void updateCommentView(Long aId);
}
