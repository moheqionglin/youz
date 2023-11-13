package com.sm.message.product;

import com.sm.message.profile.UserSimpleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 21:19
 */
public class ProductSalesDetail {
    private Integer id;
    private Integer secondCategoryId;
    private String name;
    private String size;
    private boolean sanzhung;
    private int stock;
    private BigDecimal originPrice;
    private BigDecimal currentPrice;
    private BigDecimal deliveryFee;
    private String profileImg;
    private List<String> lunboImgs;
    private List<String> detailImgs;

    private int salesCnt;
    private int commentCnt;

    private BigDecimal zhuanquPrice;

    private Long zhuanquEndTime;
    private int maxKanjiaPerson;
    private int zhuanquId;
    private String zhuanquName;
    private boolean zhuanquEnable;

    //标识该商品是否是有效的砍价商品
    private boolean validKanjiaProduct = false;
    //标识该商品自己是否已经发起过砍价
    private boolean hasKanjia = false;
    private List<UserSimpleInfo> kanjiaHelpers;
    private BigDecimal kanjiaSuccessAmount;
    private BigDecimal kanjiaLeaveAmount;
    private int kanjiaLeavePerson;
    private BigDecimal kanjiaUnitAmount;
    private BigDecimal currentKanjiaPrice;
    private BigDecimal tuangouPrice;
    public static class ProductSalesDetailRowMapper implements RowMapper<ProductSalesDetail> {

        @Override
        public ProductSalesDetail mapRow(ResultSet resultSet, int i) throws SQLException {
            ProductSalesDetail product = new ProductSalesDetail();
            if(existsColumn(resultSet, "id")){
                product.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                product.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "size")){
                product.setSize(resultSet.getString("size"));
            }
            if(existsColumn(resultSet, "sanzhung")){
                product.setSanzhung(resultSet.getBoolean("sanzhung"));
            }
            if(existsColumn(resultSet, "second_category_id")){
                product.setSecondCategoryId(resultSet.getInt("second_category_id"));
            }
            if(existsColumn(resultSet, "stock")){
                product.setStock(resultSet.getInt("stock"));
            }
            if(existsColumn(resultSet, "origin_price")){
                product.setOriginPrice(resultSet.getBigDecimal("origin_price"));
            }
            if(existsColumn(resultSet, "current_price")){
                product.setCurrentPrice(resultSet.getBigDecimal("current_price"));
            }
            if(existsColumn(resultSet, "profile_img")){
                product.setProfileImg(resultSet.getString("profile_img"));
            }
            if(existsColumn(resultSet, "zhuanquenable")){
                product.setZhuanquEnable(resultSet.getBoolean("zhuanquenable"));
            }
            if(existsColumn(resultSet, "tuangou_price")){
                product.setTuangouPrice(resultSet.getBigDecimal("tuangou_price"));
            }
            if(existsColumn(resultSet, "zhuanqu_id")){

                product.setZhuanquId(resultSet.getInt("zhuanqu_id"));
            }
            if(existsColumn(resultSet, "lunbo_imgs")){
                String lunboImgs = resultSet.getString("lunbo_imgs");
                if(StringUtils.isBlank(lunboImgs)){
                    product.setLunboImgs(Collections.emptyList());
                }else{
                    product.setLunboImgs(Arrays.asList(StringUtils.trimToEmpty(lunboImgs).split("\\|")));
                }

            }
            if(existsColumn(resultSet, "detail_imgs")){
                String detailImgs = resultSet.getString("detail_imgs");
                if(StringUtils.isBlank(detailImgs)){
                    product.setDetailImgs(Collections.emptyList());
                }else{
                    product.setDetailImgs(Arrays.asList(StringUtils.trimToEmpty(detailImgs).split("\\|")));
                }
            }
            if(existsColumn(resultSet, "sales_cnt")){
                product.setSalesCnt(resultSet.getInt("sales_cnt"));
            }
            if(existsColumn(resultSet, "comment_cnt")){
                product.setCommentCnt(resultSet.getInt("comment_cnt"));
            }

            if(existsColumn(resultSet, "zhuanqu_price")){
                product.setZhuanquPrice(resultSet.getBigDecimal("zhuanqu_price"));
            }
            if(existsColumn(resultSet, "zhuanquName")){
                product.setZhuanquName(resultSet.getString("zhuanquName"));
            }
            if(existsColumn(resultSet, "zhuanqu_endTime")){
                product.setZhuanquEndTime(resultSet.getLong("zhuanqu_endTime"));
            }
            if(existsColumn(resultSet, "tuangou_price")){
                product.setTuangouPrice(resultSet.getBigDecimal("tuangou_price"));
            }
            if(existsColumn(resultSet, "max_kanjia_person")){
                product.setMaxKanjiaPerson(resultSet.getInt("max_kanjia_person"));
            }
            return product;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public boolean isValidKanjiaProduct() {
        return validKanjiaProduct;
    }

    public BigDecimal getCurrentKanjiaPrice() {
        return currentKanjiaPrice;
    }

    public void setCurrentKanjiaPrice(BigDecimal currentKanjiaPrice) {
        this.currentKanjiaPrice = currentKanjiaPrice;
    }

    public void setValidKanjiaProduct(boolean validKanjiaProduct) {
        this.validKanjiaProduct = validKanjiaProduct;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public boolean zhuanquExpired(){
        return this.zhuanquEndTime == null || new Date().getTime() > this.zhuanquEndTime;
    }

    public int getKanjiaLeavePerson() {
        return kanjiaLeavePerson;
    }

    public void setKanjiaLeavePerson(int kanjiaLeavePerson) {
        this.kanjiaLeavePerson = kanjiaLeavePerson;
    }

    public Integer getId() {
        return id;
    }

    public boolean isHasKanjia() {
        return hasKanjia;
    }

    public int getZhuanquId() {
        return zhuanquId;
    }

    public void setZhuanquId(int zhuanquId) {
        this.zhuanquId = zhuanquId;
    }

    public boolean isZhuanquEnable() {
        return zhuanquEnable;
    }

    public void setZhuanquEnable(boolean zhuanquEnable) {
        this.zhuanquEnable = zhuanquEnable;
    }

    public BigDecimal getKanjiaUnitAmount() {
        return kanjiaUnitAmount;
    }

    public void setKanjiaUnitAmount(BigDecimal kanjiaUnitAmount) {
        this.kanjiaUnitAmount = kanjiaUnitAmount;
    }

    public void setHasKanjia(boolean hasKanjia) {
        this.hasKanjia = hasKanjia;
    }

    public String getZhuanquName() {
        return zhuanquName;
    }

    public void setZhuanquName(String zhuanquName) {
        this.zhuanquName = zhuanquName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTuangouPrice() {
        return tuangouPrice;
    }

    public void setTuangouPrice(BigDecimal tuangouPrice) {
        this.tuangouPrice = tuangouPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isSanzhung() {
        return sanzhung;
    }

    public void setSanzhung(boolean sanzhung) {
        this.sanzhung = sanzhung;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public List<String> getLunboImgs() {
        return lunboImgs;
    }

    public void setLunboImgs(List<String> lunboImgs) {
        this.lunboImgs = lunboImgs;
    }

    public List<String> getDetailImgs() {
        return detailImgs;
    }

    public void setDetailImgs(List<String> detailImgs) {
        this.detailImgs = detailImgs;
    }

    public Integer getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Integer secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public int getSalesCnt() {
        return salesCnt;
    }

    public void setSalesCnt(int salesCnt) {
        this.salesCnt = salesCnt;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }

    public Long getZhuanquEndTime() {
        return zhuanquEndTime;
    }

    public void setZhuanquEndTime(Long zhuanquEndTime) {
        this.zhuanquEndTime = zhuanquEndTime;
    }

    public int getMaxKanjiaPerson() {
        return maxKanjiaPerson;
    }

    public void setMaxKanjiaPerson(int maxKanjiaPerson) {
        this.maxKanjiaPerson = maxKanjiaPerson;
    }

    public List<UserSimpleInfo> getKanjiaHelpers() {
        return kanjiaHelpers;
    }

    public void setKanjiaHelpers(List<UserSimpleInfo> kanjiaHelpers) {
        this.kanjiaHelpers = kanjiaHelpers;
    }

    public BigDecimal getKanjiaSuccessAmount() {
        return kanjiaSuccessAmount;
    }

    public void setKanjiaSuccessAmount(BigDecimal kanjiaSuccessAmount) {
        this.kanjiaSuccessAmount = kanjiaSuccessAmount;
    }

    public BigDecimal getZhuanquPrice() {
        return zhuanquPrice;
    }

    public void setZhuanquPrice(BigDecimal zhuanquPrice) {
        this.zhuanquPrice = zhuanquPrice;
    }

    public BigDecimal getKanjiaLeaveAmount() {
        return kanjiaLeaveAmount;
    }

    public void setKanjiaLeaveAmount(BigDecimal kanjiaLeaveAmount) {
        this.kanjiaLeaveAmount = kanjiaLeaveAmount;
    }

}