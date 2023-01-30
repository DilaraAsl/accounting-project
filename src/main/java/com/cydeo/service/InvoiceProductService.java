package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);
    List<InvoiceProductDto> findByInvoiceId(Long id);
    InvoiceProductDto save(InvoiceProductDto invoiceProductDto);
    void add(InvoiceProductDto invoiceProductDto, Long invoiceId);
    InvoiceProductDto delete(Long id);
    void updateQuantityInStockSale(Long id);
    void lowerQuantityAlert(Long id);
    void updateQuantityInStockPurchase(Long id);
    void updateRemainingQuantityUponApproval(Long id);
    //void updateRemainingQuantityUponApprovalSale(Long id);
    void profit(Long id);
    List<InvoiceProductDto> findTotalPriceWithAndWithoutTax(Long  id);
    List<InvoiceProductDto> findAllInvoiceProductsOfCompany();
    List<InvoiceProductDto> findInvoiceProductByInvoiceId(Long id);

}
