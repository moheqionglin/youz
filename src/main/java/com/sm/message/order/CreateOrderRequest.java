package com.sm.message.order;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:13
 */
public class CreateOrderRequest {
    @NotNull
    private int addressId;
    @NotNull
    private BigDecimal useYongjin;
    @NotNull
    private BigDecimal useYue;
    private String yongjinCode;
    private String message;

    @NotEmpty
    @NotNull
    List<Integer> cartIds = new ArrayList<>();

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public BigDecimal getUseYongjin() {
        return useYongjin;
    }

    public void setUseYongjin(BigDecimal useYongjin) {
        this.useYongjin = useYongjin;
    }

    public BigDecimal getUseYue() {
        return useYue;
    }

    public String getYongjinCode() {
        return yongjinCode;
    }

    public void setYongjinCode(String yongjinCode) {
        this.yongjinCode = yongjinCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUseYue(BigDecimal useYue) {
        this.useYue = useYue;
    }

    public List<Integer> getCartIds() {
        return cartIds;
    }

    public void setCartIds(List<Integer> cartIds) {
        this.cartIds = cartIds;
    }
}