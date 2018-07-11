package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    //添加商品分类
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加商品参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //这个分类是可用的
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return  ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return  ServerResponse.createByErrorMessage("添加品类失败");
    }

    //修改品类名称
    public ServerResponse setCategoryName( Integer categoryId , String categoryName){
        if(categoryId == null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改商品品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);

        if(rowCount>0){
            return  ServerResponse.createBySuccessMessage("修改品类名称成功");
        }
        return  ServerResponse.createByErrorMessage("修改品类名称失败");


    }

    //查询子节点的categroy信息
    public ServerResponse<List<Category>> getChildrenParallerCategory (Integer parentId){
        List<Category> categoryList = categoryMapper.selectCategoryChilerenByParentId(parentId);
        if(categoryList.isEmpty()){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);

    }

    //查询当前节点和其递归子节点
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> set = new HashSet<Category>();
        findChileCategory(set,categoryId);
        List<Integer> list = new ArrayList<Integer>();
        if(categoryId != null){
            for(Category categoryItem : set){
                list.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(list);

    }

    //递归算法，算出子节点
    private Set<Category> findChileCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category !=null){
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChilerenByParentId(categoryId);
        for(Category childCategory : categoryList){
            findChileCategory(categorySet,childCategory.getId());
        }
        return categorySet;
    }

}
