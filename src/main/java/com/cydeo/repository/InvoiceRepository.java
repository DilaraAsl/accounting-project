package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "select * from invoices where invoice_type='PURCHASE' order by invoice_no desc limit 1", nativeQuery = true)
    Invoice retrieveLargestPurchaseInvoiceNo();

    @Query(value = "select * from invoices where invoice_type='SALES' order by invoice_no desc limit 1", nativeQuery = true)
    Invoice retrieveLargestSalesInvoiceNo();
    @Query(value="select * from invoices where invoice_status = 'APPROVED' and invoice_type='SALES'",nativeQuery = true)
    List<Invoice> retrieveApprovedSalesInvoices();
    List<Invoice> findAllByCompanyTitleAndInvoiceStatusAndInvoiceType(String companyTitle, InvoiceStatus invoiceStatus, InvoiceType invoiceType);
}


