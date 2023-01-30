package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoice_products")
@Where(clause = "is_deleted=false")
public class InvoiceProduct extends BaseEntity {

    private Integer quantity;
    private BigDecimal price;
    private Integer tax;
    private BigDecimal profitLoss;
    private Integer remainingQuantity;
    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private Product product;

    public InvoiceProduct(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, Boolean isDeleted, Integer quantity, BigDecimal price, Integer tax, BigDecimal profitLoss, Integer remainingQty, Invoice invoice, Product product) {
        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId, isDeleted);
        this.quantity = quantity;
        this.price = price;
        this.tax = tax;
        this.profitLoss = profitLoss;
        this.remainingQuantity = remainingQty;
        this.invoice = invoice;
        this.product = product;
    }

}
