package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ZhuanquCategoryDao;
import com.sm.dao.domain.ProductZhuanQuCategory;
import com.sm.message.product.KanjiaProductItem;
import com.sm.message.product.ProductListItem;
import com.sm.message.product.TejiaProductItem;
import com.sm.message.product.ZhuanquCategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
@Component
public class ZhuanQuCategoryService {
    @Autowired
    private ZhuanquCategoryDao tejiaCategoryDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductService productService;

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

    @Transactional
    public ResponseEntity delete(int categoryid) {
        if(tejiaCategoryDao.countProductByZhuanQuIt(categoryid) > 0){
            return ResponseEntity.status(HttpYzCode.CATEGORY_HAS_CHILD.getCode()).build();
        }
        tejiaCategoryDao.delete(categoryid);
        productDao.deleteZhuanqu(categoryid);
        return ResponseEntity.ok().build();
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

    public List<ZhuanquCategoryItem> getHomePageZhuanquProducts() {
        List<ZhuanquCategoryItem> zhuanqus = tejiaCategoryDao.getTejiaCategoryList().stream().filter(c -> c.isEnable()).map(t -> new ZhuanquCategoryItem(t)).collect(Collectors.toList());
        if(zhuanqus.isEmpty()){
            return new ArrayList<>();
        }
        for(Iterator<ZhuanquCategoryItem> iterator = zhuanqus.iterator(); iterator.hasNext();){
            ZhuanquCategoryItem item = iterator.next();
            List<ProductListItem> prs = productService.getTop6ProductsByZhuanQuId(item.getId());
            if(prs == null || prs.isEmpty()){
                iterator.remove();
                continue;
            }
            item.setTop6(prs);
        }

        return zhuanqus;
    }
}