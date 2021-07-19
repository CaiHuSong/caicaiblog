package com.cai.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cai.blog.dao.dos.Archives;
import com.cai.blog.dao.mapper.ArticleBodyMapper;
import com.cai.blog.dao.mapper.ArticleMapper;
import com.cai.blog.dao.mapper.ArticleTagMapper;
import com.cai.blog.dao.pojo.Article;
import com.cai.blog.dao.pojo.ArticleBody;
import com.cai.blog.dao.pojo.ArticleTag;
import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.utils.UserThreadLocal;
import com.cai.blog.vo.ArticleBodyVo;
import com.cai.blog.vo.ArticleVo;
import com.cai.blog.vo.Params.ArticleParam;
import com.cai.blog.vo.Params.PageParams;
import com.cai.blog.vo.Result;
import com.cai.blog.vo.TagVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImlp implements ArticleService{

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleTagMapper articleTagMapper;


    /**
     * 查询文章列表SQL语句实现
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.listArticle(page,pageParams.getCategoryId(),pageParams.getTagId(),pageParams.getYear(),pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }
//    @Override
//    public Result listArticle(PageParams pageParams) {
//        /**
//         * 1.分页查询 article数据库表
//         */
//
//        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
//       if (pageParams.getCategoryId()!=null){
//           //如果CategoryId参数不为空，加一个查询条件
//           queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//       }
//
//       List<Long> articleVos = new ArrayList<>();
//       if (pageParams.getTagId()!=null){
//           //如果TagId不为空，加上查询条件
//           LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
//           wrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
//           List<ArticleTag> articleTags = articleTagMapper.selectList(wrapper);
//
//           for (ArticleTag tag : articleTags) {
//               articleVos.add(tag.getArticleId());
//           }
//           if (articleVos.size()>0){
//               //add id in (1...2..)
//               queryWrapper.in(Article::getId,articleVos);
//           }
//
//       }
//        //置顶排序和按时间排序
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//
//        List<Article> records = articlePage.getRecords();
//        //不能直接返回，做一个vo对象
//        List<ArticleVo> articleVoList = copyList(records,true,true);
//
//        return Result.success(articleVoList);
//    }


    /**
     * 最热文章
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }


    /**
     * 最新文章
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     * 文章归档
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    /**
     * 文章详情
     * @param id
     * @return
     */
    @Override
    public Result findArticleById(Long id) {
        /**
         * 1.根据id查询 文章信息
         *
         */
        Article article = articleMapper.selectById(id);

        ArticleVo articleVo = copy(article, true, true, true, true);
       //查询详情成功，博客浏览量应该加一更新，但不能影响数据返回
        //使用线程池技术，开启多线程进行更新操作

        threadService.updateArticleViewCount(articleMapper,article);

        return Result.success(articleVo);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @Override
    public Result publish(ArticleParam articleParam) {
        /**
         * 1.发布文章  构建article对象
         * 2.作者id  当前登录用户
         * 3.标签 要将标签关联到表当中
         * 4.body 内容存储 article bodyid
         */
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        //插入之后会生成一个文章id
        this.articleMapper.insert(article);

        //tag
        List<TagVo> tags = articleParam.getTags();
        if (tags!=null){
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }

        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());
        return Result.success(articleVo);
    }

    /**
     * 更新评论数
     */
    @Override
    public void updateCommentView(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        Integer commentCounts = article.getCommentCounts();

        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId,articleId).set(Article::getCommentCounts,commentCounts+1);
        articleMapper.update(null,lambdaUpdateWrapper);
    }


    //copy集合
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {

        List<ArticleVo> articleVoList=new ArrayList<>();

        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }

        return articleVoList;
    }
    //copy重载
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory) {

        List<ArticleVo> articleVoList=new ArrayList<>();

        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,isBody,isCategory));
        }

        return articleVoList;
    }


    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);

        //时间转换
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }

        if (isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }

        if (isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }

        return articleVo;

    }

    @Autowired
   private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }


}
