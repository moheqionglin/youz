package com.sm.service;

import com.sm.controller.CategoryController;
import com.sm.controller.HttpYzCode;
import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.domain.ProductCategory;
import com.sm.message.product.CategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 12:51
 */
@Component
public class CategoryService {
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Autowired
    private ProductDao productDao;

    public List<CategoryItem> getChildListByParentId(int parendId) {
        return productCategoryDao.getLCategoryByParentId(parendId).stream().map(pc -> new CategoryItem(pc)).collect(Collectors.toList());
    }

    public void create(CategoryItem productCategoryItem) {
        productCategoryDao.create(productCategoryItem.generateProductCategory());
    }

    public void update(CategoryItem productCategoryItem) {
        productCategoryDao.update(productCategoryItem.generateProductCategory());
    }

    public ResponseEntity delete(CategoryController.CategoryDeleteType type, int categoryid) {
        long cnt = 0;
        switch (type){
            case FIRST:
                cnt = productCategoryDao.countSecondCategoryByFirstCategoryId(categoryid);
                break;
            case SECOND:
                cnt = productCategoryDao.countProdcutBySecondCategoryId(categoryid);
                break;
        }
        if(cnt > 0){
            return ResponseEntity.status(HttpYzCode.CATEGORY_HAS_CHILD.getCode()).build();
        }
        productCategoryDao.delete(categoryid);
        return ResponseEntity.ok().build();
    }

    public CategoryItem getProductCategory(int catalogid) {
        ProductCategory pc = productCategoryDao.getProductCategory(catalogid);
        if(pc == null){
            return null;
        }
        return new CategoryItem(pc);
    }

    public List<CategoryItem> getAllCategory() {
        List<ProductCategory> allCategory = productCategoryDao.getAllCategory();
        List<CategoryItem> result = new ArrayList<>();
        HashMap<Integer, Integer> id2ProductCnt = productDao.countBySecondCategoryId();
        Map<Integer, List<CategoryItem>> map = allCategory.stream().map(c -> new CategoryItem(c))
                .collect(Collectors.groupingBy(CategoryItem::getParentId));
        result.addAll(map.get(0));
        result.stream().forEach(c -> {
            c.setChildItems(map.get(c.getId()));
            if(c.getChildItems() != null){
                c.getChildItems().stream().sorted(Comparator.comparing(CategoryItem::getSort));
                c.getChildItems().stream().forEach(cc -> {
                    Integer cnt = id2ProductCnt.get(cc.getId());
                    cc.setProductCnt(cnt == null ? 0 : cnt);
                });
            }
        });
        return result;
    }
}