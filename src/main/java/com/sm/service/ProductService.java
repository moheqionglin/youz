package com.sm.service;

import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.dao.ProductDao;
import com.sm.message.PageResult;
import com.sm.message.order.OrderCommentsRequest;
import com.sm.message.product.*;
import com.sm.message.profile.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 21:21
 */
@Component
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductCategoryDao productCategoryDao;

    /**
     * 获取不包含 下架商品的列表
     * @param secondCategoryId
     * @param isShow
     * @param pageType
     * @param pageSize
     * @param pageNum
     * @return
     */
    public PageResult<ProductListItem> getProductsPaged(int secondCategoryId, boolean isShow, String pageType, int pageSize, int pageNum) {
        List<ProductListItem> ps = productDao.getProductsPaged(secondCategoryId, isShow, pageType, pageSize, pageNum);
        ps.forEach(pi -> {
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return new PageResult(pageSize, pageNum, -1, ps);
    }

    /**
     * 获取所有商品，包含下架商品
     * @param ids
     * @return
     */
    public List<ProductListItem> getProductsByIds(List<Integer> ids) {
        List<ProductListItem> ps = productDao.getProductsByIds(ids);
        ps.forEach(pi -> {
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return ps;
    }

    @Transactional
    public ProductSalesDetail getSalesDetail(Integer userId, int productId) {
        ProductSalesDetail productSalesDetail = productDao.getSalesDetail(productId);
        if (productSalesDetail == null){
            return null;
        }
        if(userId == null || productSalesDetail.zhuanquExpired()){
            return productSalesDetail;
        }
        productSalesDetail.setCurrentPrice(ServiceUtil.calcCurrentPrice(productSalesDetail.getCurrentPrice(), productSalesDetail.getZhuanquPrice(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquId(), productSalesDetail.getZhuanquEndTime()));
        KanjiaDetailInfo kajiaers = productDao.getKanjiaDetail(userId, productId);

        int maxKanjiaPerson = productSalesDetail.getMaxKanjiaPerson();
        if(kajiaers != null && maxKanjiaPerson > 0){
            productSalesDetail.setHasKanjia(true);
            List<UserSimpleInfo> kanjieHelpers = kajiaers.getKanjieHelpers();
            productSalesDetail.setKanjiaHelpers(kanjieHelpers);

            int kanjiaHelperCnt = kanjieHelpers == null || kanjieHelpers.isEmpty() ? 0 : kanjieHelpers.size();
            BigDecimal maxsubtract = productSalesDetail.getCurrentPrice().subtract(productSalesDetail.getZhuanquPrice());

            BigDecimal unitKanjiaAmount = maxsubtract.divide(BigDecimal.valueOf(maxKanjiaPerson));
            BigDecimal successAmount = unitKanjiaAmount.multiply(BigDecimal.valueOf(kanjiaHelperCnt)).setScale(2, RoundingMode.UP);
            productSalesDetail.setKanjiaSuccessAmount(successAmount);
            productSalesDetail.setKanjiaLeaveAmount(maxsubtract.subtract(successAmount));

        }
        return productSalesDetail;

    }

    public CreateProductRequest getEditDetail(int productId) {
        CreateProductRequest request = productDao.getEditDetail(productId);
        if(request == null){return null;}
        CategoryItem ci = productCategoryDao.getParentCategoryByChildCategory(request.getSecondCategoryId());
        if(ci != null){
            request.setFirstCategoryId(ci.getId());
            request.setFirstCategoryName(ci.getName());
            if(ci.getChildItems() != null && ci.getChildItems().get(0) != null){
                request.setSecondCategoryName(ci.getChildItems().get(0).getName());
            }
        }

        return request;
    }


    public void updateSort(int productId1,int sort1, int productId2, int sort2) {
        productDao.updateSort(productId1, sort1, productId2, sort2);
    }

    public void shangxiajia(int productId, boolean showable) {
        productDao.shangxiajia(productId, showable);
    }

    public void delete(int productId) {
        productDao.delete(productId);
    }

    public void update(CreateProductRequest product) {
        productDao.update(product);
    }

    public Integer create(CreateProductRequest product) {
        return productDao.create(product);
    }

    public void createComment(int userId, List<OrderCommentsRequest> orderCommentsRequests) {
        productDao.createComment(userId, orderCommentsRequests);
    }

    public void addSalesCount(List<Integer> ids) {
        productDao.addSalesCount(ids);
    }

    public void addCommentCount(List<Integer> ids) {
        productDao.addCommentCount(ids);
    }
}