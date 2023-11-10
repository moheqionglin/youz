package com.sm.message.admin;

import com.sm.dao.domain.ReceiveAddressManager;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.NotEmpty;
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

    public ReceiveAddressManagerInfo(){

    }

    public ReceiveAddressManagerInfo(ReceiveAddressManager receiveAddressManager){
        if(receiveAddressManager != null){
            this.id = receiveAddressManager.getId();
            this.addressName = receiveAddressManager.getAddressName();
            this.addressDetail = receiveAddressManager.getAddressDetail();

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
