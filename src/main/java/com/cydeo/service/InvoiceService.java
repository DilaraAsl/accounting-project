package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;

import java.util.Map;


public interface InvoiceService {
    InvoiceDto findById(Long id);

    InvoiceDto update(InvoiceDto invoiceDto);
    BigDecimal countTotal(InvoiceType invoiceType, InvoiceStatus invoiceStatus);

    List<ClientVendorDto> listCompanyClientsOrVendors(ClientVendorType clientVendorType);

    InvoiceDto approve(Long id);
    //InvoiceDto approvePurchase(Long id);

    void deleteInvoice(Long id);

    List<InvoiceDto> listAllInvoices();
    String generateInvoiceNo(InvoiceType invoiceType);

    String findLastRecordedPurchaseInvoice();

    String findLastRecordedSalesInvoice();

    List<InvoiceDto> calculatePriceAndSortInDescOrder(List<InvoiceDto> invoiceDtoList);

    List<InvoiceDto> findLastThreeApprovedInvoice();


    Map<String, BigDecimal> getMonthlyProfitLossMap();


    BigDecimal profitOrLoss();

    Integer calculateTaxByTaxRate(Integer taxRate, BigDecimal price);

    List<InvoiceDto> listSalesOrPurchaseInvoices(InvoiceType invoiceType);


    InvoiceDto saveInvoice(InvoiceDto invoiceDto, InvoiceType invoiceType);

    InvoiceDto generateNewInvoiceDto(InvoiceType invoiceType);

}
