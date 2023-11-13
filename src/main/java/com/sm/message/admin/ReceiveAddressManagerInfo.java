package com.sm.message.admin;

import com.sm.dao.domain.ReceiveAddressManager;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnegative;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
@ApiModel(description= "配送小区管理")
public class ReceiveAddressManagerInfo {
    private int id ;
    @ApiModelProperty(value = "小区名字")
    @NotEmpty
    private String addressName;
    @ApiModelProperty(value = "小区详细地址")
    @NotEmpty
    private String addressDetail;
    @ApiModelProperty(value = "开启团购")
    private boolean tuangouEnable;
    @ApiModelProperty(value = "开团人数")
    @NotNull
    private int tuangouThreshold;
    @ApiModelProperty(value = "运费")
    @Nonnegative
    private BigDecimal deliveryFee;

    public ReceiveAddressManagerInfo(){

    }

    public ReceiveAddressManagerInfo(ReceiveAddressManager receiveAddressManager){
        if(receiveAddressManager != null){
            this.id = receiveAddressManager.getId();
            this.addressName = receiveAddressManager.getAddressName();
            this.addressDetail = receiveAddressManager.getAddressDetail();
            this.tuangouEnable = receiveAddressManager.isTuangouEnable();
            this.tuangouThreshold = receiveAddressManager.getTuangouThreshold();
            this.deliveryFee = receiveAddressManager.getDeliveryFee();
        }
    }

    public static class ReceiveAddressManagerMapper implements RowMapper<ReceiveAddressManager> {
        @Override
        public ReceiveAddressManager mapRow(ResultSet resultSet, int i) throws SQLException {
            ReceiveAddressManager receiveAddressManagerInfo = new ReceiveAddressManager();
            if(existsColumn(resultSet, "id")){
                receiveAddressManagerInfo.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "address_name")){
                receiveAddressManagerInfo.setAddressName(resultSet.getString("address_name"));
            }
            if(existsColumn(resultSet, "address_detail")){
                receiveAddressManagerInfo.setAddressDetail(resultSet.getString("address_detail"));
            }
            if(existsColumn(resultSet, "tuangou_enable")){
                receiveAddressManagerInfo.setTuangouEnable(resultSet.getBoolean("tuangou_enable"));
            }
            if(existsColumn(resultSet, "tuangou_threshold")){
                receiveAddressManagerInfo.setTuangouThreshold(resultSet.getInt("tuangou_threshold"));
            }
            if(existsColumn(resultSet, "delivery_fee")){
                receiveAddressManagerInfo.setDeliveryFee(resultSet.getBigDecimal("delivery_fee"));
            }
            return receiveAddressManagerInfo;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public boolean isTuangouEnable() {
        return tuangouEnable;
    }

    public void setTuangouEnable(boolean tuangouEnable) {
        this.tuangouEnable = tuangouEnable;
    }

    public int getTuangouThreshold() {
        return tuangouThreshold;
    }

    public void setTuangouThreshold(int tuangouThreshold) {
        this.tuangouThreshold = tuangouThreshold;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
}
