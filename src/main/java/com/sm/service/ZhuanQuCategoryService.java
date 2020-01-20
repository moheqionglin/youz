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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    public ResponseEntity delete(int categoryid) {
        if(tejiaCategoryDao.countProductByZhuanQuIt(categoryid) > 0){
            return ResponseEntity.status(HttpYzCode.CATEGORY_HAS_CHILD.getCode()).build();
        }
        tejiaCategoryDao.delete(categoryid);
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
        //根据专区ID获取产品
        List<Integer> zhuanquIds = zhuanqus.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<ProductListItem> prs = productService.getTop6ProductsByZhuanQuIds(zhuanquIds);
        if(prs == null || prs.isEmpty()){
            return new ArrayList<>();
        }
        Map<Integer, List<ProductListItem>> pid2P = prs.stream().collect(Collectors.groupingBy(ProductListItem::getZhuanquId));
        //删除没有产品的专区
        for(Iterator<ZhuanquCategoryItem> iterator = zhuanqus.iterator(); iterator.hasNext();){
            if(!pid2P.containsKey(iterator.next().getId())){
                iterator.remove();
            }
        }
        zhuanqus.stream().forEach(z -> {
            z.setTop6(pid2P.get(z.getId()));
        });
        return zhuanqus;
    }
}