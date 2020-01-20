package com.sm.message.product;

import com.sm.dao.domain.ProductZhuanQuCategory;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
public class ZhuanquCategoryItem {
    private Integer id;
    @NotNull
    @Length(max = 100)
    private String name;
    @Length(max = 200)
    private String image;
    private boolean deleteAble;
    private boolean enable;

    private List<ProductListItem> top6 = new ArrayList<>();

    public ZhuanquCategoryItem() {
    }

    public ZhuanquCategoryItem(ProductZhuanQuCategory t) {
        this.id = t.getId();
        this.name = t.getName();
        this.image = t.getImage();
        this.deleteAble = t.isDeleteable();
        this.enable = t.isEnable();
    }
    public ProductZhuanQuCategory generateDomain() {
        ProductZhuanQuCategory t = new ProductZhuanQuCategory();
        t.setEnable(this.enable);
        t.setId(this.id);
        t.setImage(this.image);
        t.setName(this.name);
        t.setDeleteable(this.deleteAble);
        return t;
    }

    public Integer getId() {
        return id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isDeleteAble() {
        return deleteAble;
    }

    public void setDeleteAble(boolean deleteAble) {
        this.deleteAble = deleteAble;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<ProductListItem> getTop6() {
        return top6;
    }

    public void setTop6(List<ProductListItem> top6) {
        this.top6 = top6;
    }
}