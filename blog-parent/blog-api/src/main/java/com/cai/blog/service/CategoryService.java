package com.cai.blog.service;

import com.cai.blog.vo.CategoryVo;
import com.cai.blog.vo.Result;

public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    Result fingAll();

    Result findAllDetail();

    Result categoriesDetailById(Long id);
}
