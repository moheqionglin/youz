package com.sm.dao.dao;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.sm.dao.dao.ConfigDao.ConfigKeyCode.DELIVERY_FEE;
import static com.sm.dao.dao.ConfigDao.ConfigKeyCode.FREE_DELIVERY_FEE_ORDER_AMOUNT;


@Component
public class ConfigDao {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BigDecimal getDeliveryFee(){
        String deliveryFreeStr = getVal(DELIVERY_FEE, "0");
        return BigDecimal.valueOf(Double.valueOf(deliveryFreeStr));
    }

    public BigDecimal getFreeDeliveryFeeOrderAmount(){
        String deliveryFreeStr = getVal(FREE_DELIVERY_FEE_ORDER_AMOUNT, "0");
        return BigDecimal.valueOf(Double.valueOf(deliveryFreeStr));
    }
    public String getVal(ConfigKeyCode code, String defaultVal){
        final String sql = String.format("select config_value from %s where config_key_code = ? ", VarProperties.CONFIG);
        try{
            String val = jdbcTemplate.queryForObject(sql, new Object[]{code.code}, String.class);
            if(StringUtils.isEmpty(val)){
                return defaultVal;
            }
            return val;
        }catch (Exception e){
            log.error("get value error for key " + code.name, e);
        }
        return defaultVal;
    }


    public static enum ConfigKeyCode{
        DELIVERY_FEE("DELIVERY_FEE", 1),
        FREE_DELIVERY_FEE_ORDER_AMOUNT("FREE_DELIVERY_FEE_ORDER_AMOUNT", 2)
        ;
        private String name;
        private int code;

        ConfigKeyCode(String name, int code) {
            this.name = name;
            this.code = code;
        }
    }

}
