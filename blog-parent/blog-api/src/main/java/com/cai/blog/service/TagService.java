package com.cai.blog.service;

import com.cai.blog.vo.Result;
import com.cai.blog.vo.TagVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagService {

    List<TagVo> findTagByArticleId(Long articleId);

    Result hots(int limit);

    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}
