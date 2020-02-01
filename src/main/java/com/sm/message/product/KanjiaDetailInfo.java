package com.sm.message.product;

import com.sm.message.order.SimpleOrderItem;
import com.sm.message.profile.UserSimpleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:49
 */
public class KanjiaDetailInfo {
    private Integer userId;
    private Integer productId;
    private boolean terminal;
    List<Integer> uids = new ArrayList<>(1);
    List<UserSimpleInfo> kanjieHelpers = new ArrayList<>(1);


    public static class SimpleOrderItemRowMapper implements RowMapper<KanjiaDetailInfo> {
        @Override
        public KanjiaDetailInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            KanjiaDetailInfo kdi = new KanjiaDetailInfo();
            if(existsColumn(resultSet, "user_id")){
                kdi.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "product_id")){
                kdi.setProductId(resultSet.getInt("product_id"));
            }
            if(existsColumn(resultSet, "helper_ids")){
                String ids = resultSet.getString("helper_ids");
                if(StringUtils.isNoneBlank(ids)){
                    kdi.setUids( Arrays.stream(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
            }
            if(existsColumn(resultSet, "terminal")){
                kdi.setTerminal(resultSet.getBoolean("terminal"));
            }
            return kdi;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public List<Integer> getUids() {
        return uids;
    }

    public void setUids(List<Integer> uids) {
        this.uids = uids;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<UserSimpleInfo> getKanjieHelpers() {
        return kanjieHelpers;
    }

    public void setKanjieHelpers(List<UserSimpleInfo> kanjieHelpers) {
        this.kanjieHelpers = kanjieHelpers;
    }
}