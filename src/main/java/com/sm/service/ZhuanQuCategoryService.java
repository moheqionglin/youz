package com.sm.service;

import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ZhuanquCategoryDao;
import com.sm.dao.domain.ProductZhuanQuCategory;
import com.sm.message.product.KanjiaProductItem;
import com.sm.message.product.TejiaProductItem;
import com.sm.message.product.ZhuanquCategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 18:30
 */
@Component
public class ZhuanQuCategoryService {
    @Autowired
    private ZhuanquCategoryDao tejiaCategoryDao;

    @Autowired
    private ProductDao productDao;

    public List<ZhuanquCategoryItem> getZhuanquCategoryList() {
        return tejiaCategoryDao.getTejiaCategoryList().stream().map(t -> new ZhuanquCategoryItem(t)).collect(Collectors.toList());
    }

    public Integer create(ZhuanquCategoryItem productTejiaCategoryItem) {
        ProductZhuanQuCategory pc = productTejiaCategoryItem.generateDomain();
        return tejiaCategoryDao.create(pc);
    }

    public void update(ZhuanquCategoryItem productCategoryItem) {
        tejiaCategoryDao.update(productCategoryItem);
    }

    public void delete(int categoryid) {
        tejiaCategoryDao.delete(categoryid);
    }

    public void disableCategory(int categoryid, boolean ableType) {
        tejiaCategoryDao.changeAble(categoryid, ableType);
    }

    public void addCategoryProduct(int categoryid, TejiaProductItem tejiaProductItem) {
        productDao.addTejiaProudctFeature(categoryid, tejiaProductItem);
    }
    public void addCategoryProduct(int categoryid, KanjiaProductItem tejiaProductItem) {
        productDao.addKanjiaProudctFeature(categoryid, tejiaProductItem);
    }

    public void deleteCategoryProduct(int productId) {
        productDao.deleteCategoryProduct(productId);
    }
}