package com.sm.dao.dao;

import com.sm.controller.LunBoController;
import com.sm.message.lunbo.LunBoInfo;
import com.sm.message.product.SimpleCategoryInfo;
import com.sm.message.product.SimpleProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:06
 */
@Component
public class LunBoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<LunBoInfo> getAll() {
        final String sql = String.format("select id, image, link_type,link_id from %s ", VarProperties.LUNBO);
        List<LunBoInfo> lunBoInfo = jdbcTemplate.query(sql, new LunBoInfo.LunBoInfoRowMapper());

        List<LunBoInfo> productLists = lunBoInfo.stream().filter(l -> LunBoController.LinkType.PRODUCT_DETAIL.toString().equalsIgnoreCase(l.getLinkType())).collect(Collectors.toList());
        List<Integer> pids = productLists.stream().map(l -> l.getLinkId()).collect(Collectors.toList());

        List<LunBoInfo> categorys = lunBoInfo.stream().filter(l -> LunBoController.LinkType.SECOND_CATEGORY.toString().equalsIgnoreCase(l.getLinkType())).collect(Collectors.toList());
        List<Integer> secondCategoryIds = categorys.stream().map(l -> l.getLinkId()).collect(Collectors.toList());


        if(!pids.isEmpty()){
            String psql = String.format("select id, name,profile_img from %s where id in (?)", VarProperties.PRODUCTS);
            Map<Integer, List<SimpleProductInfo>> ps = jdbcTemplate.query(psql, new Object[]{pids}, new SimpleProductInfo.SimpleProudctInfoRowMapper()).stream().collect(Collectors.groupingBy(SimpleProductInfo::getId));

            productLists.stream().forEach(pl -> {
                SimpleProductInfo sp = ps.get(pl.getLinkId()).stream().findFirst().orElse(null);
                if(sp != null){
                    pl.setProductName(sp.getName());
                    pl.setProductImage(sp.getProfileImg());
                    pl.setLinkValid(sp.isShowAble());
                }else{
                    pl.setLinkValid(false);
                }
            });
        }

        if(!secondCategoryIds.isEmpty()){
            String csql = String.format("select child.id as id, child.name as name, parent.id as pid, parent.name as pname from %s child inner join %s parent on child.parent_id = parent.id where child.id in (?)", VarProperties.PRODUCT_CATEGORY, VarProperties.PRODUCT_CATEGORY);
            Map<Integer, List<SimpleCategoryInfo>> cates = jdbcTemplate.query(csql, new Object[]{secondCategoryIds}, new SimpleCategoryInfo.SimpleCategoryInfoRowMapper()).stream().collect(Collectors.groupingBy(SimpleCategoryInfo::getId));

            categorys.stream().forEach(c -> {
                SimpleCategoryInfo sci = cates.get(c.getLinkId()).stream().findFirst().orElse(null);
                if(sci != null){
                    c.setFirstCategoryId(sci.getPid());
                    c.setFirstCategoryName(sci.getPname());
                    c.setSecondCategoryId(sci.getId());
                    c.setSecondCategoryName(sci.getName());
                    c.setLinkValid(true);
                }else{
                    c.setLinkValid(false);
                }
            });
        }
        return lunBoInfo;
    }

    public void create(LunBoInfo lunbo) {
        final String sql = String.format("insert into %s(image ,link_type,link_id) values(?,?,?)", VarProperties.LUNBO);
        jdbcTemplate.update(sql, new Object[]{lunbo.getImage(), lunbo.getLinkType(), lunbo.getLinkId()});
    }

    public void update(LunBoInfo lunbo) {
        final String sql = String.format("update %s set image =?, link_type =? , link_id =? where id = ?", VarProperties.LUNBO);
        jdbcTemplate.update(sql, new Object[]{lunbo.getImage(), lunbo.getLinkType(), lunbo.getLinkId(), lunbo.getId()});
    }

    public void delete(Integer id) {
        final String sql = String.format("delete from %s where id = ?", VarProperties.LUNBO);
        jdbcTemplate.update(sql, new Object[]{id});
    }
}