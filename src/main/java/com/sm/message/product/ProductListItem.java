package com.sm.message.product;

import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 21:17
 */
public class ProductListItem {
    @NotNull
    private Integer id;
    private String name;
    private boolean sanzhung;
    private String size;
    private int stock;
    private BigDecimal originPrice;
    private BigDecimal costPrice;
    private BigDecimal currentPrice;
    private String profileImg;
    private int salesCnt;
    private int zhuanquId;
    private String zhuanquName;
    private Long zhuanquEndTime;
    private boolean zhuanquEnable;
    private BigDecimal zhuanquPrice;
    private boolean showAble;
    private int sort;
    private int maxKanjiaPerson;

    public static class ProductListItemRowMapper implements RowMapper<ProductListItem> {
        @Override
        public ProductListItem mapRow(ResultSet resultSet, int i) throws SQLException {
            ProductListItem product = new ProductListItem();
            if(existsColumn(resultSet, "id")){
                product.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                product.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "sanzhung")){
                product.setSanzhung(resultSet.getBoolean("sanzhung"));
            }
            if(existsColumn(resultSet, "sort")){
                product.setSort(resultSet.getInt("sort"));
            }
            if(existsColumn(resultSet, "max_kanjia_person")){
                product.setMaxKanjiaPerson(resultSet.getInt("max_kanjia_person"));
            }
            if(existsColumn(resultSet, "size")){
                product.setSize(resultSet.getString("size"));
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
            if(existsColumn(resultSet, "cost_price")){
                product.setCostPrice(resultSet.getBigDecimal("cost_price"));
            }
            if(existsColumn(resultSet, "profile_img")){
                product.setProfileImg(resultSet.getString("profile_img"));
            }
            if(existsColumn(resultSet, "sales_cnt")){
                product.setSalesCnt(resultSet.getInt("sales_cnt"));
            }
            if(existsColumn(resultSet, "zhuanqu_endTime")){
                product.setZhuanquEndTime(resultSet.getLong("zhuanqu_endTime"));
            }
            if(existsColumn(resultSet, "show_able")){
                product.setShowAble(resultSet.getBoolean("show_able"));
            }
            if(existsColumn(resultSet, "zhuanquenable")){
                product.setZhuanquEnable(resultSet.getBoolean("zhuanquenable"));
            }
            if(existsColumn(resultSet, "zhuanqu_id")){

                product.setZhuanquId(resultSet.getInt("zhuanqu_id"));
            }
            if(existsColumn(resultSet, "zhuanqu_price")){
                product.setZhuanquPrice(resultSet.getBigDecimal("zhuanqu_price"));
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

    public int getMaxKanjiaPerson() {
        return maxKanjiaPerson;
    }

    public void setMaxKanjiaPerson(int maxKanjiaPerson) {
        this.maxKanjiaPerson = maxKanjiaPerson;
    }

    public boolean isShowAble() {
        return showAble;
    }

    public void setShowAble(boolean showAble) {
        this.showAble = showAble;
    }

    public Integer getId() {
        return id;
    }

    public int getZhuanquId() {
        return zhuanquId;
    }

    public void setZhuanquId(int zhuanquId) {
        this.zhuanquId = zhuanquId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public boolean isZhuanquEnable() {
        return zhuanquEnable;
    }

    public void setZhuanquEnable(boolean zhuanquEnable) {
        this.zhuanquEnable = zhuanquEnable;
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

    public int getSalesCnt() {
        return salesCnt;
    }

    public void setSalesCnt(int salesCnt) {
        this.salesCnt = salesCnt;
    }

    public String getZhuanquName() {
        return zhuanquName;
    }

    public void setZhuanquName(String zhuanquName) {
        this.zhuanquName = zhuanquName;
    }

    public Long getZhuanquEndTime() {
        return zhuanquEndTime;
    }

    public void setZhuanquEndTime(Long zhuanquEndTime) {
        this.zhuanquEndTime = zhuanquEndTime;
    }

    public BigDecimal getZhuanquPrice() {
        return zhuanquPrice;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public void setZhuanquPrice(BigDecimal zhuanquPrice) {
        this.zhuanquPrice = zhuanquPrice;
    }
}