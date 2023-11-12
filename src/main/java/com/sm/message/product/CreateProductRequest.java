package com.sm.message.product;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 20:38
 */
@Valid
public class CreateProductRequest {
    private Integer id;
    @NotNull
    @Length(max = 200)
    private String name;
    @NotNull
    @Length(max = 200)
    private String size;
    @NonNull
    private Integer secondCategoryId;
    @NotNull
    private Integer firstCategoryId;
    private String firstCategoryName;
    private String secondCategoryName;
    @NonNull
    private boolean sanzhung;
    @NotNull
    private boolean yongjinAble;
    private int salesCnt;
    @NonNull
    private boolean showable;
    @NonNull
    @Length(max = 100)
    private String code;
    @NonNull
    private int stock;
    @NonNull
    private BigDecimal originPrice;
    @NonNull
    private BigDecimal costPrice;
    @NonNull
    private BigDecimal currentPrice;
    private BigDecimal offlinePrice;

    private Integer supplierId;
    private String supplierName;
    @NonNull
    private String profileImg;
    @NonNull
    @NotEmpty
    private List<String> lunboImgs;
    @NonNull
    @NotEmpty
    private List<String> detailImgs;

    private String imgToken;
    public static class CreateProductRequestRowMapper  implements RowMapper<CreateProductRequest> {

        @Override
        public CreateProductRequest mapRow(ResultSet resultSet, int i) throws SQLException {
            CreateProductRequest product = new CreateProductRequest();
            if(existsColumn(resultSet, "id")){
                product.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                product.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "size")){
                product.setSize(resultSet.getString("size"));
            }
            if(existsColumn(resultSet, "sales_cnt")){
                product.setSalesCnt(resultSet.getInt("sales_cnt"));
            }
            if(existsColumn(resultSet, "sanzhung")){
                product.setSanzhung(resultSet.getBoolean("sanzhung"));
            }
            if(existsColumn(resultSet, "show_able")){
                product.setShowable(resultSet.getBoolean("show_able"));
            }
            if(existsColumn(resultSet, "yongjin_able")){
                product.setYongjinAble(resultSet.getBoolean("yongjin_able"));
            }
            if(existsColumn(resultSet, "second_category_id")){
                product.setSecondCategoryId(resultSet.getInt("second_category_id"));
            }
            if(existsColumn(resultSet, "code")){
                product.setCode(resultSet.getString("code"));
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
            if(existsColumn(resultSet, "offline_price")){
                product.setOfflinePrice(resultSet.getBigDecimal("offline_price"));
            }
            if(existsColumn(resultSet, "profile_img")){
                product.setProfileImg(resultSet.getString("profile_img"));
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
            if(existsColumn(resultSet, "supplierId")){
                product.setSupplierId(resultSet.getInt("supplierId"));
            }
            if(existsColumn(resultSet, "supplierName")){
                product.setSupplierName(resultSet.getString("supplierName"));
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
    public Integer getId() {
        return id;
    }

    public boolean isYongjinAble() {
        return yongjinAble;
    }

    public void setYongjinAble(boolean yongjinAble) {
        this.yongjinAble = yongjinAble;
    }

    public BigDecimal getOfflinePrice() {
        return offlinePrice;
    }

    public void setOfflinePrice(BigDecimal offlinePrice) {
        this.offlinePrice = offlinePrice;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Integer secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public boolean isSanzhung() {
        return sanzhung;
    }

    public String getImgToken() {
        return imgToken;
    }

    public void setImgToken(String imgToken) {
        this.imgToken = imgToken;
    }

    public Integer getFirstCategoryId() {
        return firstCategoryId;
    }

    public void setFirstCategoryId(Integer firstCategoryId) {
        this.firstCategoryId = firstCategoryId;
    }

    public String getFirstCategoryName() {
        return firstCategoryName;
    }

    public void setFirstCategoryName(String firstCategoryName) {
        this.firstCategoryName = firstCategoryName;
    }

    public String getSecondCategoryName() {
        return secondCategoryName;
    }

    public void setSecondCategoryName(String secondCategoryName) {
        this.secondCategoryName = secondCategoryName;
    }

    public void setSanzhung(boolean sanzhung) {
        this.sanzhung = sanzhung;
    }

    public int getSalesCnt() {
        return salesCnt;
    }

    public void setSalesCnt(int salesCnt) {
        this.salesCnt = salesCnt;
    }

    public boolean isShowable() {
        return showable;
    }

    public void setShowable(boolean showable) {
        this.showable = showable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @NonNull
    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(@NonNull String profileImg) {
        this.profileImg = profileImg;
    }

    @NonNull
    public List<String> getLunboImgs() {
        return lunboImgs;
    }

    public void setLunboImgs(@NonNull List<String> lunboImgs) {
        this.lunboImgs = lunboImgs;
    }

    @NonNull
    public List<String> getDetailImgs() {
        return detailImgs;
    }

    public void setDetailImgs(@NonNull List<String> detailImgs) {
        this.detailImgs = detailImgs;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}