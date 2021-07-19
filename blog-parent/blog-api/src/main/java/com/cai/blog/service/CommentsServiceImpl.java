package com.cai.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cai.blog.dao.mapper.CommentMapper;
import com.cai.blog.dao.pojo.Comment;
import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.utils.UserThreadLocal;
import com.cai.blog.vo.CommentVo;
import com.cai.blog.vo.Params.CommentParam;
import com.cai.blog.vo.Result;
import com.cai.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleService articleService;

    /**
     * 根据文章id查询所有的评论列表
     * @param id
     * @return
     */
    @Override
    public Result commentsByArticleId(Long id) {
        /**
         * 1.根据文章id查询评论列表  从comment查
         * 2.根据作者id 查询作者信息
         * 3.判断 如果level=1 查是否有子评论
         * 4.如果有，根据评论id进行查询（parent_id）
         */

        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,id);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVos=copyList(comments);

        return Result.success(commentVos);
    }


    @Override
    public Result comment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentMapper.insert(comment);

        //更新文章评论数
        articleService.updateCommentView(commentParam.getArticleId());

        return Result.success(null);

    }


    //copy数据库查询List对象到voList对象
    private List<CommentVo> copyList(List<Comment> comments) {

        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment: comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    //copy数据库查询对象到VO对象
    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if (1==level){
            Long id = comment.getId();
            List<CommentVo> commentVos=findCommentsByParentId(id);
            commentVo.setChildrens(commentVos);
        }
        //toUser
        if (level>1){
            Long toUid = comment.getToUid();
            commentVo.setToUser(toUid);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        return copyList(comments);
    }
}
