package com.sm.dao.dao;

import com.sm.dao.domain.ProductCategory;
import com.sm.dao.rowMapper.ProductCategoryRowMapper;
import com.sm.message.product.CategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 * id,name, image , sort,parent_id, created_time , modified_time
 */
@Component
public class ProductCategoryDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 增加目录
     * @param productCategory
     */
    public void create(ProductCategory productCategory){
        String sql = "insert into product_category(name, image, sort, parent_id) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, new Object[]{productCategory.getName(), productCategory.getImage(), productCategory.getSort(), productCategory.getParentId()});
    }

    /**
     * 删除目录
     */
    public void delete(int categoryId){
        String sql = "delete from product_category where id = ?";
        jdbcTemplate.update(sql, new Object[]{categoryId});
    }

    /**
     * 更改 只能改改 一级目录的 图片，名字，和排序。
     *     只能修改 二级分类的  名字和排序。
     * 不允许修改二级分类的 父分类
     * @param productCategory
     */
    public void update(ProductCategory productCategory){
        String sql = "update product_category set name = ? ," +
                "image = ? , sort = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{productCategory.getName(), productCategory.getImage(), productCategory.getSort(), productCategory.getId()});

    }

    /**
     *
     * 如果 parentCategoryId = 0 那么查询的都是一级分类
     * 如果非0，查询的是对应的二级分类
     * @param parentCategoryId
     * @return
     */
    public List<ProductCategory> getLCategoryByParentId(int parentCategoryId){
        String sql = "select id,name, image , sort,parent_id from product_category where parent_id = ?";
        return jdbcTemplate.query(sql, new Object[]{parentCategoryId}, new ProductCategoryRowMapper());
    }

    /**
     *
     * 获取所有的catalog
     * @return
     */
    public List<ProductCategory> getAllCategory(){
        String sql = "select id, name, image , sort,parent_id from product_category ";
        return jdbcTemplate.query(sql, new ProductCategoryRowMapper());
    }

    public ProductCategory getProductCategory(int catagoryid) {
        String sql = "select id, name, image , sort,parent_id from product_category where id = ?";
        return jdbcTemplate.query(sql, new Object[]{catagoryid}, new ProductCategoryRowMapper()).stream().findFirst().orElse(null);
    }

    public CategoryItem getParentCategoryByChildCategory(int childCategoryId){
        if(childCategoryId <= 0){
            return null;
        }
        final String sql = String.format("select t1.id as id, t1.name as name,t2.id as pid, t2.name as pname from %s t1 inner join %s t2 on t1.parent_id = t2.id where t1.id = ?", VarProperties.PRODUCT_CATEGORY, VarProperties.PRODUCT_CATEGORY);
        try{
            Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap(sql, new Object[]{childCategoryId});
            CategoryItem productCategory = new CategoryItem();
            productCategory.setId(Integer.valueOf(stringObjectMap.get("pid").toString()));
            productCategory.setName(stringObjectMap.get("pname").toString());

            CategoryItem cci = new CategoryItem();
            cci.setId(Integer.valueOf(stringObjectMap.get("id").toString()));
            cci.setName(stringObjectMap.get("name").toString());
            productCategory.getChildItems().add(cci);
            return productCategory;
        }catch (Exception e){
            return null;
        }


    }
}