package com.cai.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cai.blog.dao.pojo.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> findTagByArticleId(Long articleId);

    List<Long> findHotsTagIds(int limit);

    List<Tag> findTagsByTagIds(@Param("tagIds") List<Long> tagIdS);
}
