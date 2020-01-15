package com.sm.message.product;

import com.sm.dao.domain.ProductSupplier;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 19:58
 */
public class SupplierInfo {
    private Integer id;
    @NotNull
    @Length(max = 100)
    private String name;
    @NotNull
    @Length(max = 50)
    private String contactPerson;
    @NotNull
    @Length(max = 12)
    private String phone;

    public SupplierInfo(ProductSupplier s) {
        this.id = s.getId();
        this.name = s.getName();
        this.contactPerson = s.getContactPerson();
        this.phone = s.getPhone();
    }

    public ProductSupplier generateDomain(){
        ProductSupplier ps = new ProductSupplier();
        ps.setId(this.id);
        ps.setName(this.name);
        ps.setContactPerson(this.contactPerson);
        ps.setPhone(this.phone);
        return ps;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}