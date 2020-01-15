package com.sm.service;

import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.domain.ProductCategory;
import com.sm.message.product.CategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    public List<CategoryItem> getChildListByParentId(int parendId) {
        return productCategoryDao.getLCategoryByParentId(parendId).stream().map(pc -> new CategoryItem(pc)).collect(Collectors.toList());
    }

    public void create(CategoryItem productCategoryItem) {
        productCategoryDao.create(productCategoryItem.generateProductCategory());
    }

    public void update(CategoryItem productCategoryItem) {
        productCategoryDao.update(productCategoryItem.generateProductCategory());
    }

    public void delete(int categoryid) {
        productCategoryDao.delete(categoryid);
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
        Map<Integer, List<CategoryItem>> map = allCategory.stream().map(c -> new CategoryItem(c))
                .collect(Collectors.groupingBy(CategoryItem::getParentId));
        result.addAll(map.get(0));
        result.stream().forEach(c -> {
            c.setChildItems(map.get(c.getId()));
        });
        return result;
    }
}