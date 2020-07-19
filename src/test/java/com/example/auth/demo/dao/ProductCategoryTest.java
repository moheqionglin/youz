package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.domain.ProductCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 12:22
 */
public class ProductCategoryTest extends BaseTest {
    @Autowired
    private ProductCategoryDao productCategoryDao;

    public void createTest(){
        for (int i = 0; i < 11; i++) {
            productCategoryDao.create(generate(0, i));
            for (int j = 0; j < 10; j++) {
                productCategoryDao.create(generate(i, j));
            }
        }
    }

    private ProductCategory generate(int parentId, int index) {

        ProductCategory productCategory = new ProductCategory();
        productCategory.setParentId(parentId);
        productCategory.setName((parentId == 0 ? "一级目录-":"二级目录-" +parentId + "/") + index);
        return productCategory;
    }

    public void deleteTest(){
        for (int i = 1; i <= 11; i++) {
            productCategoryDao.delete(i);
        }
    }

    public void updateTest(){
        ProductCategory pc = generate(1, 1);
        pc.setName("更新-" + pc.getName());
        pc.setImage("跟新" + pc.getImage());
        pc.setId(145);
        productCategoryDao.update(pc);
    }

    @Test
    public void getLevelByParentIdTest(){
        for(int i =0 ; i < 11; i ++){
            List<ProductCategory> lCategoryByParentId = productCategoryDao.getLCategoryByParentId(i);
            System.out.println(lCategoryByParentId);
        }
    }

    @Test
    public void getLevelByParentIdTest2(){
        List<ProductCategory> lCategoryByParentId = productCategoryDao.getLCategoryByParentId(-1);
        System.out.println(lCategoryByParentId);
    }
    @Test
    public void getAllCategoryTest(){
        System.out.println(productCategoryDao.getAllCategory());
    }

    @Test
    public void getChildByParentIdTest(){
        System.out.println(productCategoryDao.getProductCategory(-1));
    }
}