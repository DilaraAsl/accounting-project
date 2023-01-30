package com.cydeo.repository;

import com.cydeo.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    List<InvoiceProduct> findByInvoice_Id(Long id);

    @Query(value = "SELECT SUM(ip.quantity) FROM InvoiceProduct ip WHERE ip.invoice.id = ?1 AND ip.product.id=?2")
    Integer sumQuantityFromInvoiceProduct(Long id,Long productId);

    @Query(value = "SELECT distinct ip.product.id FROM InvoiceProduct ip WHERE ip.invoice.id = ?1")
    List<Long> getDistinctProductIdByInvoiceId(Long id);

    @Query(value = "SELECT ip.id FROM InvoiceProduct ip WHERE ip.invoice.id = ?1")
    List<Long> getInvoiceProductIdByInvoiceId(Long id);

    @Query(value = "SELECT ((ip.price*ip.quantity)+((ip.price*ip.quantity)*ip.tax)/100) FROM InvoiceProduct ip WHERE ip.id = ?1")
    BigDecimal getTotalPerInvoiceProductId(Long id);

    @Query(value = "SELECT ip.product.id FROM InvoiceProduct ip WHERE ip.id = ?1")
    Long getProductIdByInvoiceProductId(Long id);


    @Query(value = "SELECT ip.id FROM InvoiceProduct ip JOIN Invoice iv ON iv.id=ip.invoice.id WHERE iv.invoiceStatus = 'APPROVED' AND iv.invoiceType = 'PURCHASE' AND iv.company.id=?1 order by ip.id asc ")
    List<Long> getApprovedPurchaseInvoiceProductId(Long companyId);

    List<InvoiceProduct> findAllByInvoice_Id(Long  id);

@Query("select i from InvoiceProduct i " +
        "where i.invoice.company.id = :id and i.invoice.invoiceStatus = 'APPROVED' " +
        "order by i.invoice.date DESC")
List<InvoiceProduct> getInvoiceProductsByCompany(@Param("id") Long companyId);
}
