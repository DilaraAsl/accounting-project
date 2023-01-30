package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.exception.ProductLowLimitAlertException;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final ProductRepository productRepository;
    private final CompanyService companyService;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository,
                                     MapperUtil mapperUtil,
                                     ProductRepository productRepository,
                                     CompanyService companyService,
                                     @Lazy InvoiceService invoiceService,
                                     InvoiceRepository invoiceRepository) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.productRepository = productRepository;
        this.companyService = companyService;
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
    }


    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new InvoiceProductNotFoundException("Invoice product not found"));
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }


    @Override
    public List<InvoiceProductDto> findByInvoiceId(Long id) {
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findByInvoice_Id(id);
        return invoiceProductList.stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .peek(invoiceProduct -> invoiceProduct
                        .setTotal((invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()))
                                .multiply(BigDecimal.valueOf(invoiceProduct.getTax()))
                                .divide(BigDecimal.valueOf(100))).add((BigDecimal.valueOf(invoiceProduct.getPrice().doubleValue()))
                                .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceProductDto save(InvoiceProductDto invoiceProductDto) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        InvoiceProduct saved = invoiceProductRepository.save(invoiceProduct);
        return mapperUtil.convert(saved, new InvoiceProductDto());
    }

    @Override
    public void add(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        Invoice invoice = mapperUtil.convert(invoiceService.findById(invoiceId), new Invoice());
        invoiceProduct.setInvoice(invoice);
        invoiceProduct.setRemainingQuantity(0);
        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public InvoiceProductDto delete(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new InvoiceProductNotFoundException("Invoice product not found"));
        invoiceProduct.setIsDeleted(true);
        InvoiceProduct deleted = invoiceProductRepository.save(invoiceProduct);
        return mapperUtil.convert(deleted, new InvoiceProductDto());
    }

    @Override
    public void updateQuantityInStockPurchase(Long id) {
        List<Long> productIdList = invoiceProductRepository.getDistinctProductIdByInvoiceId(id);
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        if (invoice.getInvoiceType().equals(InvoiceType.PURCHASE)) {
            for (Long each : productIdList) {
                Product product = productRepository.findProductById(each);
                product.setQuantityInStock(product.getQuantityInStock() + invoiceProductRepository.sumQuantityFromInvoiceProduct(id, each));
                productRepository.save(product);
            }
        }
    }

    @Override
    public void updateQuantityInStockSale(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        if (invoice.getInvoiceType().equals(InvoiceType.SALES)) {
            List<Long> productIdListSale = invoiceProductRepository.getDistinctProductIdByInvoiceId(id);
            for (Long each : productIdListSale) {
                Product product = productRepository.findProductById(each);
                final int stock = product.getQuantityInStock() - invoiceProductRepository.sumQuantityFromInvoiceProduct(id, each);
                if (stock < 0)
                    throw new ProductNotFoundException("Stock of " + product.getName() + " is not enough to approve this invoice. Please update the invoice.");
                product.setQuantityInStock(stock);
                productRepository.save(product);
            }
        }
    }

    @Override
    public void lowerQuantityAlert(Long id) {
        List<Long> productIdListSale = invoiceProductRepository.getDistinctProductIdByInvoiceId(id);
        List<String> lowQuantityProductList = new ArrayList<>();
        for (Long each : productIdListSale) {
            Product product = productRepository.findProductById(each);
            if (product.getQuantityInStock() < 5) {
                lowQuantityProductList.add(product.getName());
            }
        }
        if (!lowQuantityProductList.isEmpty()) {
            throw new ProductLowLimitAlertException("Stock of " + lowQuantityProductList + " decreased below low limit");
        }
    }

    @Override
    public void updateRemainingQuantityUponApproval(Long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        if (invoice.getInvoiceType().equals(InvoiceType.PURCHASE)) {
            List<Long> InvoiceProductIdList = invoiceProductRepository.getInvoiceProductIdByInvoiceId(id);
            for (Long each : InvoiceProductIdList) {
                InvoiceProduct invoiceProduct = invoiceProductRepository.findById(each).get();
                invoiceProduct.setRemainingQuantity(invoiceProduct.getQuantity());
                System.out.println(invoiceProduct.getRemainingQuantity());
                if (invoiceProduct.getProfitLoss() == null) {
                    invoiceProduct.setProfitLoss(BigDecimal.valueOf(0));
                }
                invoiceProductRepository.save(invoiceProduct);
            }
        } else {
            List<Long> InvoiceProductIdList = invoiceProductRepository.getInvoiceProductIdByInvoiceId(id);
            for (Long each : InvoiceProductIdList) {
                InvoiceProduct invoiceProduct = invoiceProductRepository.findById(each).get();
                invoiceProduct.setRemainingQuantity(0);
                System.out.println("SALE" + invoiceProduct.getRemainingQuantity());
                if (invoiceProduct.getProfitLoss() == null) {
                    invoiceProduct.setProfitLoss(BigDecimal.valueOf(0));
                }
                invoiceProductRepository.save(invoiceProduct);
            }
        }
    }


    @Override
    public void profit(Long id) {
        List<Long> invoiceProductIdList = invoiceProductRepository.getInvoiceProductIdByInvoiceId(id); // list of invoiceProducts under the approved sales invoice
        Long retrievedProductId;
        Integer retrievedSalesQuantity;
        BigDecimal profitLoss;
        for (Long eachSoldInvoiceProductId : invoiceProductIdList) {
            InvoiceProduct invoiceProductSale = invoiceProductRepository.findById(eachSoldInvoiceProductId).orElseThrow();
            retrievedProductId = invoiceProductRepository.getProductIdByInvoiceProductId(eachSoldInvoiceProductId); // product id is found
            retrievedSalesQuantity = invoiceProductSale.getQuantity(); // get the quantity of the sold product
            profitLoss = invoiceProductRepository.getTotalPerInvoiceProductId(eachSoldInvoiceProductId).subtract(calculateInitialCostOfGoods(retrievedProductId, retrievedSalesQuantity));
            invoiceProductSale.setProfitLoss(profitLoss);
            invoiceProductRepository.save(invoiceProductSale);
        }
    }

    @Override
    public List<InvoiceProductDto> findTotalPriceWithAndWithoutTax(Long id) {
        return invoiceProductRepository.findAllByInvoice_Id(id).stream().map(
                invoiceProduct -> {
                    InvoiceProductDto invoiceProductDto = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
                    invoiceProductDto.setTotal(totalPriceWithTax(invoiceProductDto));
                    return invoiceProductDto;
                }).collect(Collectors.toList());
    }

    private BigDecimal totalPriceWithTax(InvoiceProductDto invoiceProduct) {
        BigDecimal totalPrice = invoiceProduct.getPrice()
                .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()));
        return totalPrice.add(BigDecimal.valueOf(invoiceService.calculateTaxByTaxRate
                (invoiceProduct.getTax(), totalPrice)));
    }


    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsOfCompany() {
        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();
        Company company = mapperUtil.convert(companyDto, new Company());

        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.getInvoiceProductsByCompany(company.getId());

        return invoiceProductList.stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    public BigDecimal calculateInitialCostOfGoods(Long productId, Integer salesQuantity) {
        List<Long> approvedPurchaseProductIdList = invoiceProductRepository.getApprovedPurchaseInvoiceProductId(companyService.getCompanyDtoByLoggedInUser().getId());
        BigDecimal costOfGoodsSold = BigDecimal.valueOf(0.00);
        BigDecimal totalInitialCostOfGoodsSold = BigDecimal.valueOf(0.00);

        for (Long eachPurchase : approvedPurchaseProductIdList) {
            InvoiceProduct purchasedInvoiceProduct = invoiceProductRepository.findById(eachPurchase).get();
            if (invoiceProductRepository.getProductIdByInvoiceProductId(eachPurchase) == productId) {
                if (purchasedInvoiceProduct.getRemainingQuantity() == 0) {
                    continue;
                }
                if (purchasedInvoiceProduct.getRemainingQuantity() == salesQuantity) {
                    costOfGoodsSold = purchasedInvoiceProduct.getPrice().multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getRemainingQuantity()))
                            .multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getTax()))
                            .divide(BigDecimal.valueOf(100)).add((BigDecimal.valueOf(purchasedInvoiceProduct.getPrice().doubleValue()))
                                    .multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getRemainingQuantity())));
                    purchasedInvoiceProduct.setRemainingQuantity(0);
                    totalInitialCostOfGoodsSold = totalInitialCostOfGoodsSold.add(costOfGoodsSold);
                    invoiceProductRepository.save(purchasedInvoiceProduct);
                    break;
                } else if (purchasedInvoiceProduct.getRemainingQuantity() > salesQuantity) {
                    costOfGoodsSold = purchasedInvoiceProduct.getPrice().multiply(BigDecimal.valueOf(salesQuantity))
                            .multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getTax()))
                            .divide(BigDecimal.valueOf(100)).add((BigDecimal.valueOf(purchasedInvoiceProduct.getPrice().doubleValue()))
                                    .multiply(BigDecimal.valueOf(salesQuantity)));
                    purchasedInvoiceProduct.setRemainingQuantity(purchasedInvoiceProduct.getRemainingQuantity() - salesQuantity);
                    totalInitialCostOfGoodsSold = totalInitialCostOfGoodsSold.add(costOfGoodsSold);
                    invoiceProductRepository.save(purchasedInvoiceProduct);
                    break;
                } else {
                    costOfGoodsSold = purchasedInvoiceProduct.getPrice().multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getRemainingQuantity()))
                            .multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getTax()))
                            .divide(BigDecimal.valueOf(100)).add((BigDecimal.valueOf(purchasedInvoiceProduct.getPrice().doubleValue()))
                                    .multiply(BigDecimal.valueOf(purchasedInvoiceProduct.getRemainingQuantity())));
                    salesQuantity = salesQuantity - purchasedInvoiceProduct.getRemainingQuantity();
                    totalInitialCostOfGoodsSold = totalInitialCostOfGoodsSold.add(costOfGoodsSold);
                    purchasedInvoiceProduct.setRemainingQuantity(0);
                    invoiceProductRepository.save(purchasedInvoiceProduct);
                }
            }
        }
        return totalInitialCostOfGoodsSold;
    }


    public List<InvoiceProductDto> findInvoiceProductByInvoiceId(Long id) {
        return invoiceProductRepository.findByInvoice_Id(id).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

}
