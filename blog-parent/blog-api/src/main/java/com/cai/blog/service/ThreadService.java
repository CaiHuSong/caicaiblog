package com.cai.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cai.blog.dao.mapper.ArticleMapper;
import com.cai.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {

        int viewCounts = article.getViewCounts();
        Article article1 = new Article();
        article1.setViewCounts(viewCounts+1);
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId,article.getId());
        //多设置一个条件，保证线程安全
        //在每一次更新前查询view count是否于对象相同，保证在更新前数据不会改变
        lambdaUpdateWrapper.eq(Article::getViewCounts,viewCounts);
        articleMapper.update(article1,lambdaUpdateWrapper);

    }
}
