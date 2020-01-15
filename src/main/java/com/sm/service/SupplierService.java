package com.sm.service;

import com.sm.dao.dao.SupplierDao;
import com.sm.dao.domain.ProductSupplier;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.product.SupplierInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 19:56
 */
@Component
public class SupplierService {
    @Autowired
    private SupplierDao supplierDao;


    public List<SupplierInfo> getAll() {
         return supplierDao.getAll().stream().map(s -> new SupplierInfo(s)).collect(Collectors.toList());
    }

    public Integer create(SupplierInfo supplier) {
        return supplierDao.create(supplier.generateDomain());
    }

    public void update(SupplierInfo supplierInfo) {
        supplierDao.update(supplierInfo);
    }

    /**
     * 判断是否有关联的商品，有的话不能删除。
     * @param supplierId
     */
    @Transactional
    public void delete(int supplierId) {
        supplierDao.delete(supplierId);
    }

    public SupplierInfo get(int supplierId) {
        ProductSupplier productSupplier = supplierDao.get(supplierId);
        if(productSupplier == null){
            return null;
        }
        return new SupplierInfo(productSupplier);
    }
}