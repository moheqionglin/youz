package com.sm.message.product;


import com.sm.dao.domain.ProductCategory;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
public class CategoryItem {

    private Integer id;
    @NotNull
    @Length(max = 100)
    private String name;
    @Length(max = 200)
    private String image;
    @NotNull
    private int sort;
    @NotNull
    private Integer parentId;

    List<CategoryItem> childItems = new ArrayList<>();

    public CategoryItem() {
    }
    public CategoryItem(ProductCategory productCategory) {
        this.id = productCategory.getId();
        this.name = productCategory.getName();
        this.image = productCategory.getImage();
        this.sort = productCategory.getSort();
        this.parentId = productCategory.getParentId();
    }
    public ProductCategory generateProductCategory(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(this.id);
        productCategory.setName(this.name);
        productCategory.setImage(this.image);
        productCategory.setSort(this.sort);
        productCategory.setParentId(this.parentId);
        return productCategory;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<CategoryItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<CategoryItem> childItems) {
        this.childItems = childItems;
    }

    @Override
    public String toString() {
        return "ProductCategoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", sort=" + sort +
                ", parentId=" + parentId +
                '}';
    }
}