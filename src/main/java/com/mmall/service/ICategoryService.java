package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public ServiceResponse addCategory(String categoryName, Integer ParentId);

    public ServiceResponse updateCategoryName(String categoryName, Integer categoryId);

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    public ServiceResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
