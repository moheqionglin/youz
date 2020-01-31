package com.sm.service;

import com.sm.controller.ProductController;
import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ShoppingCartDao;
import com.sm.message.order.OrderCommentsRequest;
import com.sm.message.product.*;
import com.sm.message.profile.UserSimpleInfo;
import com.sm.message.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ShoppingCartDao shoppingCartDao;
    @Autowired
    private ProductCategoryDao productCategoryDao;

    @Autowired
    private SearchService searchService;


    @Autowired
    private ServiceUtil serviceUtil;
    /**
     * 获取不包含 下架商品的列表
     * @param isShow
     * @param pageType
     * @param pageSize
     * @param pageNum
     * @return
     */
    public List<ProductListItem> getProductsPaged(ProductController.CategoryType categoryType, int categoryId, boolean isShow, String pageType, int pageSize, int pageNum) {
        List<ProductListItem> ps = productDao.getProductsPaged(categoryType, categoryId, isShow, pageType, pageSize, pageNum);
        ps.forEach(pi -> {
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return ps;
    }

    /**
     * 获取所有商品，包含下架商品
     * @param ids
     * @return
     */
    public List<ProductListItem> getAllContanisXiajiaProductsByIds(List<Integer> ids) {
        List<ProductListItem> ps = productDao.getAllContanisXiajiaProductsByIds(ids);
        ps.forEach(pi -> {
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return ps;
    }

    public List<ProductListItem> getTop6ProductsByZhuanQuIds(List<Integer> zhuanquIds) {
        if(zhuanquIds == null || zhuanquIds.isEmpty()){
            return new ArrayList<>();
        }
        List<ProductListItem> productsByZhuanQuIds = productDao.getTop6ProductsByZhuanQuIds(zhuanquIds);
        productsByZhuanQuIds.stream().forEach(pi ->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return productsByZhuanQuIds;
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
        productSalesDetail.setZhuanquName(ServiceUtil.zhuanquName(productSalesDetail.getZhuanquId(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquEndTime()));
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
        request.setImgToken(serviceUtil.getNewImgToken());
        return request;
    }


    public void updateSort(int productId1,int sort1, int productId2, int sort2) {
        productDao.updateSort(productId1, sort1, productId2, sort2);
    }

    public void shangxiajia(int productId, boolean showable) {
        productDao.shangxiajia(productId, showable);
    }

    @Transactional
    public void delete(int productId) {
        productDao.delete(productId);
        shoppingCartDao.deleteCartByPid(productId);
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

    public List<ProductListItem> adminSearch(SearchRequest searchRequest, int pageSize, int pageNum) {
        List<ProductListItem> admin = productDao.search(searchRequest, pageSize, pageNum, "ADMIN");
        admin.stream().forEach(pi->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return admin;
    }
    public List<ProductListItem> search(Integer userid, String term, int pageSize, int pageNum) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setType(SearchRequest.SearchType.ALL);
        searchRequest.setShow(true);
        searchRequest.setSearchTerm(term);
        if(userid != null){
            searchService.addMySearchTerm(userid, term);
        }
        List<ProductListItem> notadmin = productDao.search(searchRequest, pageSize, pageNum, "NOTADMIN");
        notadmin.stream().forEach(pi ->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
        });
        return notadmin;
    }

    public Integer getProductIdByCode(String code) {
        return productDao.getProductIdByCode(code);
    }

    public void subStock(HashMap<Integer, Integer> pid2cnt) {
        productDao.subStock(pid2cnt);
    }
}