package com.sm.dao.dao;

import com.sm.controller.ProductController;
import com.sm.message.order.OrderCommentsRequest;
import com.sm.message.product.*;
import com.sm.message.profile.UserSimpleInfo;
import com.sm.message.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 21:21
 * 注意不显示 show_able = false的
 */
@Component
public class ProductDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ProductListItem> getProductsPaged(ProductController.CategoryType categoryType, int categoryId, boolean isShow, String pageType, int pageSize, int pageNum) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        String filterByCategory = "";
        if(ProductController.CategoryType.FIRST.equals(categoryType)){
            filterByCategory = " and first_category_id = ? ";
        }else if(ProductController.CategoryType.SECOND.equals(categoryType)){
            filterByCategory = " and second_category_id = ? ";
        }else if(ProductController.CategoryType.ZHUANQU.equals(categoryType)){
            filterByCategory = " and zhuanqu_id = ? ";
        }

        String adminPageColumns = "ADMIN".equalsIgnoreCase(pageType) ? ", t1.size as size, t1.sort as sort, t1.cost_price as cost_price" : "";
        String sort = "ADMIN".equalsIgnoreCase(pageType) ? "order by t1.sort asc " : "order by t1.sales_cnt desc, t1.sort asc ";
        final String sql = String.format("select t1.id as id, t1.name as name ,sanzhung,show_able,stock,origin_price,current_price,profile_img,sales_cnt, zhuanqu_id, t2.enable as zhuanquenable, zhuanqu_price,zhuanqu_endTime " + adminPageColumns +
                " from %s as t1 left join %s as t2 on t1.zhuanqu_id = t2.id " +
                " where t1.show_able = ? %s %s limit ?, ?", VarProperties.PRODUCTS, VarProperties.PRODUCT_ZHUANQU_CATEGORY, filterByCategory, sort);
        Object[] pams = new Object[]{isShow, categoryId, startIndex, pageSize};
        if(ProductController.CategoryType.ALL.equals(categoryType)){
            pams = new Object[]{isShow, startIndex, pageSize};
        }
        return jdbcTemplate.query(sql, pams, new ProductListItem.ProductListItemRowMapper());
    }

    public ProductSalesDetail getSalesDetail(int productId) {

        final String sql = String.format("select t1.id as id, t1.name as name, second_category_id, size, sanzhung,stock,origin_price,current_price,profile_img,lunbo_imgs,detail_imgs,sales_cnt,zhuanqu_id, comment_cnt, t2.enable as zhuanquenable, zhuanqu_price ,zhuanqu_endTime, max_kanjia_person " +
                " from %s as t1 left join %s as t2 on t1.zhuanqu_id = t2.id " +
                " where t1.show_able = true and t1.id = ?", VarProperties.PRODUCTS, VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        return jdbcTemplate.query(sql, new Object[]{productId}, new ProductSalesDetail.ProductSalesDetailRowMapper()).stream().findFirst().orElse(null);
    }

    public KanjiaDetailInfo getKanjiaDetail(Integer userId, int productId) {
        final String sql1 = String.format("select helper_ids, terminal from %s where user_id = ? and product_id = ?", VarProperties.PRODUCT_KANJIA);
        try{
            Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap(sql1, new Object[]{userId, productId});
            String ids = String.valueOf(stringObjectMap.get("helper_ids"));
            boolean terminal = (boolean)stringObjectMap.get("terminal");
            List<Integer> uids = Arrays.stream(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
            KanjiaDetailInfo udi = new KanjiaDetailInfo();
            udi.setTerminal(terminal);
            if(ids.isEmpty()){
                return udi;
            }
            final String sql = String.format("select id, nick_name, head_picture from %s where id in ( :ids ) ", VarProperties.USERS);
            if(uids != null && !uids.isEmpty()){
                List<UserSimpleInfo> query = namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", uids), new UserSimpleInfo.UserSimpleInfoRowMapper());
                udi.getKanjieHelpers().addAll(query);
                udi.setUserId(userId);
                udi.setProductId(productId);
            }
            return udi;
        }catch (Exception e){
            return null;
        }
    }

    public CreateProductRequest getEditDetail(int productId) {
        final String sql = String.format("select t1.id as id, t1.name as name ,size, second_category_id, sanzhung,show_able, code, stock,origin_price,cost_price,current_price,profile_img,lunbo_imgs,detail_imgs,sales_cnt, t2.name as supplierName , t2.id as  supplierId "+
                " from %s as t1 left join %s as t2 on t1.supplier_id = t2.id " +
                " where t1.id = ? ", VarProperties.PRODUCTS, VarProperties.PRODUCT_SUPPLIERS);
        return jdbcTemplate.query(sql, new Object[]{productId}, new CreateProductRequest.CreateProductRequestRowMapper()).stream().findFirst().orElse(null);
    }

    @Transactional
    public void updateSort(int productId1, int sort1, int productId2, int sort2) {
        final String sql = String.format("update %s set sort = ? where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.batchUpdate(sql, Arrays.asList(new Object[]{sort1, productId1}, new Object[]{sort2, productId2}));

    }

    public void shangxiajia(int productId, boolean showAble) {
        final String sql = String.format("update %s set show_able = ? where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.update(sql, new Object[]{showAble, productId});
    }

    public void delete(int productId) {
        final String sql = String.format("delete from %s where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.update(sql, new Object[]{productId});
    }

    public void update(CreateProductRequest product) {

        final String sql = String.format("update %s set name = :name, size = :size, second_category_id = :second_category_id, first_category_id = :first_category_id" +
                " ,sanzhung = :sanzhung, show_able = :show_able, code=:code, stock=:stock, origin_price=:origin_price , " +
                " cost_price =:cost_price, current_price=:current_price, profile_img=:profile_img, lunbo_imgs=:lunbo_imgs, " +
                " detail_imgs=:detail_imgs, supplier_id=:supplier_id where id = :id", VarProperties.PRODUCTS);
        HashMap<String, Object> parsms = new HashMap<>();
        parsms.put("name", product.getName());
        parsms.put("size", product.getSize());
        parsms.put("second_category_id", product.getSecondCategoryId());
        parsms.put("first_category_id", product.getFirstCategoryId());
        parsms.put("sanzhung", product.isSanzhung());
        parsms.put("show_able", product.isShowable());
        parsms.put("code", product.getCode());
        parsms.put("stock", product.getStock());
        parsms.put("origin_price", product.getOriginPrice());
        parsms.put("cost_price", product.getCostPrice());
        parsms.put("current_price", product.getCurrentPrice());
        parsms.put("profile_img", product.getProfileImg());
        parsms.put("detail_imgs", product.getDetailImgs() == null ? "": product.getDetailImgs().stream().collect(Collectors.joining("|")));
        parsms.put("lunbo_imgs", product.getLunboImgs() == null ? "": product.getLunboImgs().stream().collect(Collectors.joining("|")));
        parsms.put("supplier_id", product.getSupplierId());
        parsms.put("id", product.getId());
        namedParameterJdbcTemplate.update(sql, parsms);
    }

    public Integer create(CreateProductRequest product) {
        Integer sort = 0;
        try{
            sort = jdbcTemplate.queryForObject("select id from " + VarProperties.PRODUCTS + " ordre by id desc limit 1", Integer.class);
        }catch (Exception e){

        }


        final String sql = String.format("insert into %s (name,size,first_category_id,second_category_id,show_able,sanzhung,code,stock,origin_price,cost_price,current_price,supplier_id, sort, profile_img, lunbo_imgs ,detail_imgs) values(" +
                ":name,:size,:first_category_id, :second_category_id,:show_able,:sanzhung,:code,:stock,:origin_price,:cost_price,:current_price,:supplier_id, :sort, :profile_img, :lunbo_imgs ,:detail_imgs)", VarProperties.PRODUCTS);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("name", product.getName());
        sqlParameterSource.addValue("size", product.getSize());
        sqlParameterSource.addValue("first_category_id", product.getFirstCategoryId());
        sqlParameterSource.addValue("second_category_id", product.getSecondCategoryId());
        sqlParameterSource.addValue("show_able", product.isShowable());
        sqlParameterSource.addValue("sanzhung", product.isSanzhung());
        sqlParameterSource.addValue("code", product.getCode());
        sqlParameterSource.addValue("stock", product.getStock());
        sqlParameterSource.addValue("origin_price", product.getOriginPrice());
        sqlParameterSource.addValue("cost_price", product.getCostPrice());
        sqlParameterSource.addValue("current_price", product.getCurrentPrice());
        sqlParameterSource.addValue("supplier_id", product.getSupplierId());
        sqlParameterSource.addValue("sort", sort + 1);
        sqlParameterSource.addValue("profile_img", product.getProfileImg());
        sqlParameterSource.addValue("detail_imgs", product.getDetailImgs() == null ? "": product.getDetailImgs().stream().collect(Collectors.joining("|")));
        sqlParameterSource.addValue("lunbo_imgs", product.getLunboImgs() == null ? "": product.getLunboImgs().stream().collect(Collectors.joining("|")));
        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void addTejiaProudctFeature(int categoryid, TejiaProductItem tejiaProductItem) {
        final String sql = String.format("update %s set zhuanqu_id = ? , zhuanqu_price = ?, zhuanqu_endTime = ?  where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.update(sql, new Object[]{tejiaProductItem.getCategoryId(), tejiaProductItem.getTejiaPrice(), tejiaProductItem.getEndTime(), tejiaProductItem.getProductId()});
    }

    public void addKanjiaProudctFeature(int categoryid, KanjiaProductItem kanjiaProductItem) {
        final String sql = String.format("update %s set zhuanqu_id = ? , zhuanqu_price = ?, zhuanqu_endTime = ? , max_kanjia_person = ? where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.update(sql, new Object[]{kanjiaProductItem.getCategoryId(), kanjiaProductItem.getKanjiaPrice(), kanjiaProductItem.getEndTime(), kanjiaProductItem.getMaxKanjiaPerson(), kanjiaProductItem.getProductId()});
    }

    public void deleteCategoryProduct(int productId) {
        final String sql = String.format("update %s set zhuanqu_id = 0 , zhuanqu_price = null, zhuanqu_endTime = null , max_kanjia_person = null where id = ?", VarProperties.PRODUCTS);
        jdbcTemplate.update(sql, new Object[]{productId});
    }

    public List<ProductListItem> getAllContanisXiajiaProductsByIds(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>(1);
        }
        final String sql = String.format("select t1.id as id, t1.name as name ,cost_price, sanzhung,stock,show_able,origin_price,current_price,profile_img,sales_cnt, zhuanqu_id, size, t2.enable as zhuanquenable, zhuanqu_price " +
                " from %s as t1 left join %s as t2 on t1.zhuanqu_id = t2.id " +
                " where t1.id in (:ids)", VarProperties.PRODUCTS, VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", ids), new ProductListItem.ProductListItemRowMapper());
    }

    public void addSalesCount(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return ;
        }
        final String sql = String.format("update %s set sales_cnt = sales_cnt + 1 where id in (:ids)", VarProperties.PRODUCTS);
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("ids", ids));
    }

    public void createComment(int userId, List<OrderCommentsRequest> orderCommentsRequests) {
        final String sql = String.format("insert into %s(user_id, product_id, good, comment, images, product_name, product_profile_img, product_size) values(?,?,?,?,?, ?,?,?)", VarProperties.PRODUCT_COMMENT);
        List<Object[]> collect = orderCommentsRequests.stream()
                .map(o -> new Object[]{userId,
                        o.getProductId(),
                        o.isGood(),
                        o.getMessage(),
                        o.getImages() != null ? o.getImages().stream().collect(Collectors.joining("|")) : "",
                        o.getProductName(),
                        o.getProductProfileImg(),
                        o.getProductSize()
                })
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, collect);
    }

    public void addCommentCount(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return ;
        }
        final String sql = String.format("update %s set comment_cnt = comment_cnt + 1 where id in (:ids)", VarProperties.PRODUCTS);
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("ids", ids));
    }

    public List<ProductListItem> search(SearchRequest searchRequest, int pageSize, int pageNum, String pageType) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        String adminPageColumns = "ADMIN".equalsIgnoreCase(pageType) ? ", t1.size as size, t1.cost_price as cost_price" : "";

        List<Object> params = new ArrayList<>();
        params.add(searchRequest.isShow());
        params.add("%"+searchRequest.getSearchTerm()+"%");
        String filterByCategory = "";
        if(SearchRequest.SearchType.FIRST.equals(searchRequest.getType())){
            filterByCategory = " and first_category_id = ?";
            params.add(searchRequest.getCategoryId());
        }else if(SearchRequest.SearchType.SECOND.equals(searchRequest.getType())){
            filterByCategory = " and second_category_id = ?";
            params.add(searchRequest.getCategoryId());
        }else if (SearchRequest.SearchType.ZHUAN_QU.equals(searchRequest.getType())){
            filterByCategory = " and zhuanqu_id = ?";
            params.add(searchRequest.getCategoryId());
        }else if (SearchRequest.SearchType.ALL_NOT_ZHUANQU.equals(searchRequest.getType())){
            filterByCategory = " and zhuanqu_id <=0  ";
        }

        params.add(startIndex);
        params.add(pageSize);
        final String sql = String.format("select t1.id as id, t1.name as name ,sanzhung,show_able,stock,origin_price,current_price,profile_img,sales_cnt, zhuanqu_id, t2.enable as zhuanquenable, zhuanqu_price , zhuanqu_endTime" +adminPageColumns +
                " from %s as t1 left join %s as t2 on t1.zhuanqu_id = t2.id " +
                " where t1.show_able = ? and t1.name like ? %s order by t1.sort asc limit ?, ?", VarProperties.PRODUCTS, VarProperties.PRODUCT_ZHUANQU_CATEGORY,
                filterByCategory);

        return jdbcTemplate.query(sql, params.toArray(), new ProductListItem.ProductListItemRowMapper());
    }

    public List<ProductListItem> getTop6ProductsByZhuanQuIds(List<Integer> zhuanquIds) {
        final String sql = String.format("select t1.id as id, t1.name as name ,sanzhung,stock,show_able,origin_price,current_price,profile_img,sales_cnt, zhuanqu_id, t2.enable as zhuanquenable, zhuanqu_price,zhuanqu_endTime" +
                " from %s as t1 left join %s as t2 on t1.zhuanqu_id = t2.id " +
                " where zhuanqu_id in (:ids) and show_able = true and zhuanqu_endTime > :time", VarProperties.PRODUCTS, VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        HashMap<String, Object> pams = new HashMap<>();
        pams.put("ids", zhuanquIds);
        pams.put("time", new Date().getTime());
        return namedParameterJdbcTemplate.query(sql, pams, new ProductListItem.ProductListItemRowMapper());
    }

    public Integer getProductIdByCode(String code) {
        final String sql = String.format("select id from %s where code = ? and show_able= true", VarProperties.PRODUCTS);
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{code}, Integer.class);
        }catch (Exception e){
            return null;
        }
    }

    public HashMap<Integer, Integer> countBySecondCategoryId() {
        final String sql = String.format("select second_category_id as id, count(1) as cnt from %s group by second_category_id", VarProperties.PRODUCTS);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        HashMap<Integer, Integer> result = new HashMap<>();
        maps.stream().forEach(l -> {
            result.put(Integer.valueOf(l.get("id").toString()), Integer.valueOf(l.get("cnt").toString()));
        });
        return result;
    }
}