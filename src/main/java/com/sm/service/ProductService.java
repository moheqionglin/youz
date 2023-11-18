package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.ProductController;
import com.sm.dao.dao.ConfigDao;
import com.sm.dao.dao.ProductCategoryDao;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ShoppingCartDao;
import com.sm.message.ResultJson;
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
    private ConfigDao configDao;
    @Autowired
    private ServiceUtil serviceUtil;
    @Autowired
    private AddressService addressService;
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
            if(!("ADMIN".equals(pageType) && ProductController.CategoryType.ZHUANQU.equals(categoryType))){
                //专区管理页面看到的currentPrice就是实际的售价
                pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
            }
            pi.setValidKanjiaProduct(ServiceUtil.zhuanquValid(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime())
                    && ServiceUtil.isKanjia(pi.getZhuanquId()));

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
            pi.setValidKanjiaProduct(ServiceUtil.zhuanquValid(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime())
                    && ServiceUtil.isKanjia(pi.getZhuanquId()));
        });
        return ps;
    }

    public List<ProductListItem> getTop6ProductsByZhuanQuId(Integer zhuanquId) {
        if(zhuanquId == null){
            return new ArrayList<>();
        }
        List<ProductListItem> productsByZhuanQuIds = productDao.getTop6ProductsByZhuanQuId(zhuanquId);
        productsByZhuanQuIds.stream().forEach(pi ->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
            pi.setValidKanjiaProduct(ServiceUtil.zhuanquValid(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime())
                    && ServiceUtil.isKanjia(pi.getZhuanquId()));
        });
        return productsByZhuanQuIds;
    }

    /**
     * 如果 kanjiaUserid 不为空 代表，帮别人砍价页面，要看的是别人的砍价情况。
     * 如果kanjiaUserid为空，表明自己进入自己的砍价页面，看自己的砍价情况。
     * @param userId
     * @param productId
     * @param kanjiaUserid
     * @return
     */
    @Transactional
    public ProductSalesDetail getSalesDetail(Integer userId, int productId, Integer kanjiaUserid) {
        ProductSalesDetail productSalesDetail = productDao.getSalesDetail(productId);
        if (productSalesDetail == null){
            return null;
        }
        productSalesDetail.setDeliveryFee(configDao.getDeliveryFee());
        if(userId == null || productSalesDetail.zhuanquExpired()){
            return productSalesDetail;
        }
        productSalesDetail.setZhuanquName(ServiceUtil.zhuanquName(productSalesDetail.getZhuanquId(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquEndTime()));
        productSalesDetail.setCurrentPrice(ServiceUtil.calcCurrentPrice(productSalesDetail.getCurrentPrice(), productSalesDetail.getZhuanquPrice(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquId(), productSalesDetail.getZhuanquEndTime()));
        productSalesDetail.setValidKanjiaProduct(ServiceUtil.zhuanquValid(productSalesDetail.getZhuanquId(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquEndTime())
                && ServiceUtil.isKanjia(productSalesDetail.getZhuanquId()));
        //product上面的 专区有效，且是砍价专区的时候
        if(ServiceUtil.zhuanquValid(productSalesDetail.getZhuanquId(), productSalesDetail.isZhuanquEnable(), productSalesDetail.getZhuanquEndTime())
        && ServiceUtil.isKanjia(productSalesDetail.getZhuanquId())){
            int trueKanjiaUserid = userId;
            if(kanjiaUserid != null && kanjiaUserid > 0){
                trueKanjiaUserid = kanjiaUserid;
            }
            KanjiaDetailInfo kajiaers = productDao.getKanjiaDetail(trueKanjiaUserid, productId);

            int maxKanjiaPerson = productSalesDetail.getMaxKanjiaPerson();
            if(kajiaers != null && !kajiaers.isTerminal() && maxKanjiaPerson > 0 ){
                productSalesDetail.setHasKanjia(true);
                List<UserSimpleInfo> kanjieHelpers = kajiaers.getKanjieHelpers();
                productSalesDetail.setKanjiaHelpers(kanjieHelpers);
                BigDecimal maxsubtract = productSalesDetail.getOriginPrice().subtract(productSalesDetail.getZhuanquPrice());

                BigDecimal unitKanjiaAmount = maxsubtract.divide(BigDecimal.valueOf(maxKanjiaPerson), 2, RoundingMode.UP);
                productSalesDetail.setKanjiaUnitAmount(unitKanjiaAmount);
                //确保砍价人数要 >0 <最大个数
                if(kanjieHelpers == null || kanjieHelpers.isEmpty()){
                    productSalesDetail.setKanjiaSuccessAmount(BigDecimal.ZERO);
                    productSalesDetail.setKanjiaLeaveAmount(maxsubtract);
                    productSalesDetail.setKanjiaLeavePerson(maxKanjiaPerson);
                    productSalesDetail.setCurrentKanjiaPrice(productSalesDetail.getOriginPrice());
                }else if (kanjieHelpers.size() >= maxKanjiaPerson){
                    productSalesDetail.setKanjiaSuccessAmount(maxsubtract);
                    productSalesDetail.setKanjiaLeaveAmount(BigDecimal.ZERO);
                    productSalesDetail.setKanjiaLeavePerson(0);
                    productSalesDetail.setCurrentKanjiaPrice(productSalesDetail.getZhuanquPrice());
                }else{
                    BigDecimal successAmount = unitKanjiaAmount.multiply(BigDecimal.valueOf(kanjieHelpers.size())).setScale(2, RoundingMode.UP);
                    productSalesDetail.setKanjiaSuccessAmount(successAmount);
                    productSalesDetail.setKanjiaLeaveAmount(maxsubtract.subtract(successAmount));
                    productSalesDetail.setKanjiaLeavePerson(maxKanjiaPerson - kanjieHelpers.size());
                    productSalesDetail.setCurrentKanjiaPrice(productSalesDetail.getOriginPrice().subtract(successAmount).setScale(2, RoundingMode.UP));
                }
            }
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
        pageNum += 1;
        List<ProductListItem> admin = productDao.search(searchRequest, pageSize, pageNum, "ADMIN");
        admin.stream().forEach(pi->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
            pi.setValidKanjiaProduct(ServiceUtil.zhuanquValid(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime())
                    && ServiceUtil.isKanjia(pi.getZhuanquId()));
        });
        return admin;
    }
    public List<ProductListItem> search(Integer userid, String term, int pageSize, int pageNum, int addressId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setType(SearchRequest.SearchType.ALL);
        searchRequest.setShow(true);
        searchRequest.setSearchTerm(term);
        if(userid != null){
            searchService.addMySearchTerm(userid, term);
            searchService.addHotSearch(term);
        }
        boolean tuangouEnable = addressService.tuangouEnable(addressId);
        List<ProductListItem> notadmin = productDao.search(searchRequest, pageSize, pageNum, "NOTADMIN");
        notadmin.stream().forEach(pi ->{
            pi.setZhuanquName(ServiceUtil.zhuanquName(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime()));
            pi.setCurrentPrice(ServiceUtil.calcCurrentPrice(pi.getCurrentPrice(), pi.getZhuanquPrice(), pi.isZhuanquEnable(), pi.getZhuanquId(), pi.getZhuanquEndTime()));
            pi.setValidKanjiaProduct(ServiceUtil.zhuanquValid(pi.getZhuanquId(), pi.isZhuanquEnable(), pi.getZhuanquEndTime())
                    && ServiceUtil.isKanjia(pi.getZhuanquId()));
            pi.setTuangouEnable(tuangouEnable);
        });
        return notadmin;
    }

    public Integer getProductIdByCode(String code, boolean isAdmin) {
        return productDao.getProductIdByCode(code, isAdmin);
    }

    public void subStock(HashMap<Integer, Integer> pid2cnt) {
        productDao.subStock(pid2cnt);
    }

    public Boolean existsHelpOtherKanjia(int selfUserid, Integer otherUserid, Integer pid) {
        List<Integer> helpOtherKanjiaIds = productDao.getHelpOtherKanjiaIds(otherUserid, pid);
        return helpOtherKanjiaIds != null && !helpOtherKanjiaIds.isEmpty() && helpOtherKanjiaIds.contains(selfUserid);
    }

    public ResultJson helpOtherKanjia(int selfUserid, Integer otherUserid, Integer pid) {
        List<Integer> helpOtherKanjiaIds = productDao.getHelpOtherKanjiaIds(otherUserid, pid);

        if(helpOtherKanjiaIds != null && !helpOtherKanjiaIds.isEmpty() && helpOtherKanjiaIds.contains(selfUserid)){
            return ResultJson.failure(HttpYzCode.KANJIA_HELP_OTHER_EXISTS);
        }
        helpOtherKanjiaIds.add(selfUserid);
        productDao.helpOtherKanjia(helpOtherKanjiaIds, otherUserid, pid);
        return ResultJson.ok();
    }

    public ResultJson startMyKanjia(int uid, Integer pid) {
        if(productDao.existsMyKanjia(uid, pid)){
            return ResultJson.failure(HttpYzCode.KANJIA_SELF_EXISTS);
        }
        productDao.startMyKanjia(uid, pid);
        return ResultJson.ok();
    }

    public void terminateKanjia(int userId, Integer pid) {
        productDao.terminateKanjia(userId, pid);
    }

    public List<Integer> getAllKanjiaingPids(int uid) {
        return productDao.getAllKanjiaingPids(uid);
    }

    public ProductListItem getSingleEditListProduct(Integer productId) {
        return productDao.getSingleEditListProduct(productId);
    }
}