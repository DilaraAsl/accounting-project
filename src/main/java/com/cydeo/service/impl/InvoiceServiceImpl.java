package com.cydeo.service.impl;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.Product;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.ProductLowLimitAlertException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final CompanyService companyService;
    private final ClientVendorService clientVendorService;
    private final MapperUtil mapperUtil;


    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceProductService invoiceProductService, CompanyService companyService,
                              ClientVendorService clientVendorService, MapperUtil mapperUtil) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.companyService = companyService;
        this.clientVendorService = clientVendorService;
        this.mapperUtil = mapperUtil;
    }

    public Integer calculateTaxByTaxRate(Integer taxRate, BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(taxRate).divide(BigDecimal.valueOf(100))).intValue();
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        InvoiceDto invoiceDTO = mapperUtil.convert(invoice, new InvoiceDto());

        if (!invoiceProductService.findTotalPriceWithAndWithoutTax(invoiceDTO.getId()).isEmpty()) {
            invoiceDTO.setTax(BigDecimal.valueOf(taxOfInvoice(invoiceDTO.getId())));
            invoiceDTO.setPrice(priceOfInvoiceWithoutTax(invoiceDTO.getId()));
            invoiceDTO.setTotal(priceOfInvoiceWithTax(invoiceDTO.getId()));
        }
        return invoiceDTO;
    }

    private int taxOfInvoice(Long invoiceId) {
        return priceOfInvoiceWithTax(invoiceId).intValue() - priceOfInvoiceWithoutTax(invoiceId).intValue();
    }

    private BigDecimal priceOfInvoiceWithoutTax(Long invoiceId) {
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findTotalPriceWithAndWithoutTax(invoiceId);
        return invoiceProducts.stream().map(invoiceProduct -> invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private BigDecimal priceOfInvoiceWithTax(Long invoiceId) {
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findTotalPriceWithAndWithoutTax(invoiceId);

        return invoiceProducts.stream().map(invoiceProductDto -> invoiceProductDto.getTotal()).reduce(BigDecimal::add).get();

    }

    @Override
    public List<InvoiceDto> listAllInvoices() {
        Long companyId = companyService.getCompanyDtoByLoggedInUser().getId();
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getCompany().getId().equals(companyId))
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());
    }


    @Override
    public List<InvoiceDto> listSalesOrPurchaseInvoices(InvoiceType invoiceType) {

        List<InvoiceDto> salesOrPurchase = listAllInvoices().stream()
                .filter(invoice -> invoice.getInvoiceType().equals(invoiceType))
                .collect(Collectors.toList());
        return calculatePriceAndSortInDescOrder(salesOrPurchase);
    }

    public String findLastRecordedPurchaseInvoice() {
        InvoiceDto invoiceDto = mapperUtil
                .convert(invoiceRepository.retrieveLargestPurchaseInvoiceNo(), new InvoiceDto());
        return invoiceDto.getInvoiceNo();

    }

    @Override
    public String findLastRecordedSalesInvoice() {
        InvoiceDto invoiceDto = mapperUtil
                .convert(invoiceRepository.retrieveLargestSalesInvoiceNo(), new InvoiceDto());
        return invoiceDto.getInvoiceNo();

    }

    public String generateInvoiceNo(InvoiceType invoiceType) {
        int number;
        if (invoiceType.equals(InvoiceType.PURCHASE)) {
            number = invoiceNoConverter(findLastRecordedPurchaseInvoice());
            number++;
            String formatted = String.format("%03d", number);
            return "P-" + formatted;
        }
        number = invoiceNoConverter(findLastRecordedSalesInvoice());
        number++;
        String formatted = String.format("%03d", number);
        return "S-" + formatted;

    }

    @Override
    public InvoiceDto generateNewInvoiceDto(InvoiceType invoiceType) {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo(generateInvoiceNo(invoiceType));
        invoiceDto.setDate(LocalDate.now());
        invoiceDto.setInvoiceType(invoiceType);
        return invoiceDto;
    }

    @Override
    public List<ClientVendorDto> listCompanyClientsOrVendors(ClientVendorType clientVendorType) {
        return clientVendorService.listAllClientVendors()
                .stream()
                .filter(clientOrVendor -> clientOrVendor.getClientVendorType().equals(clientVendorType))
                .filter(companySelect -> companySelect.getCompany().getId()
                        .equals(companyService.getCompanyDtoByLoggedInUser().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public InvoiceDto approve(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setDate(LocalDate.now());
        invoiceRepository.save(invoice);
        invoiceProductService.updateRemainingQuantityUponApproval(id);
        if (invoice.getInvoiceType().equals(InvoiceType.SALES)){
            invoiceProductService.updateQuantityInStockSale(id);
            invoiceProductService.profit(id);
        }else{
            invoiceProductService.updateQuantityInStockPurchase(id);
        }
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    public static int invoiceNoConverter(String invoiceNo) {
        return Integer.parseInt(invoiceNo.substring(2));
    }


    @Override
    public InvoiceDto update(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceRepository.findById(invoiceDto.getId()).orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        ClientVendor clientVendor = mapperUtil.convert(invoiceDto.getClientVendor(), new ClientVendor());
        invoice.setClientVendor(clientVendor);
        invoiceRepository.save(invoice);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
        invoiceProductService.findByInvoiceId(id).stream()
                .forEach(invoiceProductDto -> invoiceProductService.delete(invoiceProductDto.getId()));

    }


    public InvoiceDto saveInvoice(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        invoiceDto.setCompany(companyService.getCompanyDtoByLoggedInUser());
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(invoiceType);
        Invoice invoice = invoiceRepository.save(mapperUtil.convert(invoiceDto, new Invoice()));
        return mapperUtil.convert(invoice, new InvoiceDto());
    }


    @Override
    public List<InvoiceDto> calculatePriceAndSortInDescOrder(List<InvoiceDto> invoiceDtoList) {
        return invoiceDtoList.stream()
                .sorted(Comparator.comparing(InvoiceDto::getDate).reversed())
                .peek(invoiceDto ->
                        {
                            Long invoiceId = invoiceDto.getId();
                            List<InvoiceProductDto> invoiceProductList = invoiceProductService.findByInvoiceId(invoiceId);

                            BigDecimal totalPrice = invoiceProductList.stream().map(invoiceProductDto -> invoiceProductDto.getPrice()
                                    .multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal totalTax = invoiceProductList.stream().map(invoiceProductDto -> invoiceProductDto.getPrice()
                                            .multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())).multiply(BigDecimal.valueOf(invoiceProductDto.getTax()))
                                            .divide(BigDecimal.valueOf(100)))

                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            invoiceDto.setPrice(totalPrice);
                            invoiceDto.setTax(totalTax);
                            invoiceDto.setTotal(totalPrice.add(totalTax));
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> findLastThreeApprovedInvoice() {
        List<InvoiceDto> approvedInvoice = listAllInvoices().stream()
                .filter(invoice -> invoice.getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .sorted(Comparator.comparing(InvoiceDto::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        return calculatePriceAndSortInDescOrder(approvedInvoice);
    }


    @Override
    public BigDecimal countTotal(InvoiceType invoiceType, InvoiceStatus invoiceStatus) {
        List<InvoiceDto> approvedInvoices = listSalesOrPurchaseInvoices(invoiceType).stream()
                .filter(invoiceDto -> invoiceDto.getInvoiceStatus().equals(invoiceStatus))
                .collect(Collectors.toList());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (InvoiceDto eachInvoice : approvedInvoices) {
            totalPrice = totalPrice.add(eachInvoice.getTotal());
        }
        return totalPrice;
    }

    @Override
    public BigDecimal profitOrLoss() {
        return invoiceRepository.findAllByCompanyTitleAndInvoiceStatusAndInvoiceType(
                        companyService.getCompanyDtoByLoggedInUser().getTitle(),
                        InvoiceStatus.APPROVED, InvoiceType.SALES)
                .stream()
                .map(invoice -> invoiceProductService.findByInvoiceId(invoice.getId()).stream()
                        .map(InvoiceProductDto::getProfitLoss).reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getMonthlyProfitLossMap() {

        Map<String, BigDecimal> profitLossDataMap = new LinkedHashMap<>();
        List<Invoice> salesInvoices = invoiceRepository
                .findAllByCompanyTitleAndInvoiceStatusAndInvoiceType(
                        companyService.getCompanyDtoByLoggedInUser().getTitle(),
                        InvoiceStatus.APPROVED, InvoiceType.SALES);
        List<InvoiceProductDto> salesInvoiceProducts = salesInvoices.stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed())
                .flatMap(invoice -> invoiceProductService.findByInvoiceId(invoice.getId()).stream())
                .collect(Collectors.toList());
        for (InvoiceProductDto invoiceProduct : salesInvoiceProducts) {
            String key = invoiceProduct.getInvoice().getDate().getYear() + " " + invoiceProduct.getInvoice().getDate().getMonth();
            BigDecimal profitLoss = invoiceProduct.getProfitLoss();
            profitLossDataMap.put(key, profitLossDataMap.getOrDefault(key, BigDecimal.ZERO).add(profitLoss));
        }
        return profitLossDataMap;

    }
}