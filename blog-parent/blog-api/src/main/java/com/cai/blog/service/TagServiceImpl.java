package com.cai.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cai.blog.dao.mapper.TagMapper;
import com.cai.blog.dao.pojo.Tag;
import com.cai.blog.vo.Result;
import com.cai.blog.vo.TagVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService{

    @Autowired
    private TagMapper tagMapper;

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    @Override
    public List<TagVo> findTagByArticleId(Long articleId) {
       List<Tag> tags=tagMapper.findTagByArticleId(articleId);
        return copyList(tags);
    }

    @Override
    public Result hots(int limit) {
        /**
         * 1.标签所拥有的文章数最多  最热标签
         * 2.查询 根据tag_id 分组，计数重大到小排序，取limit个
         */
        List<Long> tagIdS = tagMapper.findHotsTagIds(limit);
        //如果为空，返回一个空list
        if (CollectionUtils.isEmpty(tagIdS)){
            return Result.success(Collections.emptyList());
        }
        //需要的是Tag对象
        List<Tag> tagList = tagMapper.findTagsByTagIds(tagIdS);
        return Result.success(tagList);
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tagList = this.tagMapper.selectList(queryWrapper);

        return Result.success(copyList(tagList));
    }

    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        List<Tag> tags = this.tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));
    }

    @Override
    public Result findDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);
        return Result.success(copy(tag));
    }
}
