package com.cydeo.entity;


import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ProductUnit;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
@Table(name = "products")
public class Product extends BaseEntity {

    private String name;

    private int quantityInStock;
    private int lowLimitAlert;
    @Enumerated(EnumType.STRING)
    private ProductUnit productUnit;

    @ManyToOne
    private Category category;
    @OneToMany(mappedBy = "product")
    private List<InvoiceProduct>  invoiceProducts;

    public Product(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, Boolean isDeleted, String name, int quantityInStock, int lowLimitAlert, ProductUnit productUnit, Category category) {
        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId, isDeleted);
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.lowLimitAlert = lowLimitAlert;
        this.productUnit = productUnit;
        this.category = category;
    }
}
