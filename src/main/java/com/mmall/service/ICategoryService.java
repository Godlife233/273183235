package com.mmall.service;

import com.mmall.common.ServerResponse;


import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse setCategoryName( Integer categoryId , String categoryName);

     ServerResponse getChildrenParallerCategory (Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

}
