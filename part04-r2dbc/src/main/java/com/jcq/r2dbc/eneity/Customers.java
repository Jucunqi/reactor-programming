package com.jcq.r2dbc.eneity;

import lombok.Getter;
import org.springframework.data.relational.core.mapping.Table;


/**
 * 数据库实体类
 * @author : jucunqi
 * @since : 2025/1/17
 */
@Getter
@Table("customers")
public class Customers {

    private final Integer customerId;
    private final String customerName;

    public Customers(Integer customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "Customers{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
