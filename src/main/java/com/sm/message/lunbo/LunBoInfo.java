package com.sm.message.lunbo;

import org.springframework.jdbc.core.RowMapper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:07
 * image varchar(200),
 *     link_type varchar(20),
 *     link_id int
 */
@Valid
public class LunBoInfo {
    private Integer id;
    @NotNull
    private String image;
    //不跳转，详情，二级分类
    @NotNull
    private String linkType;
    @NotNull
    private Integer linkId;


    //标识跳转的地方是否合法，不合法不跳转。
    private boolean linkValid = true;
    private String productName;
    private String ProductImage;

    private Integer firstCategoryId;
    private String firstCategoryName;
    private Integer secondCategoryId;
    private String secondCategoryName;

    public static class LunBoInfoRowMapper implements RowMapper<LunBoInfo> {
        @Override
        public LunBoInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            LunBoInfo lunbo = new LunBoInfo();
            if(existsColumn(resultSet, "id")){
                lunbo.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "image")){
                lunbo.setImage(resultSet.getString("image"));
            }
            if(existsColumn(resultSet, "link_type")){
                lunbo.setLinkType(resultSet.getString("link_type"));
            }
            if(existsColumn(resultSet, "link_id")){
                lunbo.setLinkId(resultSet.getInt("link_id"));
            }
            return lunbo;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public String getImage() {
        return image;
    }

    public boolean isLinkValid() {
        return linkValid;
    }

    public void setLinkValid(boolean linkValid) {
        this.linkValid = linkValid;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
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

    public Integer getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Integer secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public String getSecondCategoryName() {
        return secondCategoryName;
    }

    public void setSecondCategoryName(String secondCategoryName) {
        this.secondCategoryName = secondCategoryName;
    }
}