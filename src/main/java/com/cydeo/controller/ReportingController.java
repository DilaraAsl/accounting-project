package com.cydeo.controller;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportingController {


    private final InvoiceProductService invoiceProductService;
    private final InvoiceService invoiceService;

    public ReportingController(InvoiceProductService invoiceProductService, InvoiceService invoiceService) {
        this.invoiceProductService = invoiceProductService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/stockData")
    public String showStockReports(Model model){

        List<InvoiceProductDto> invoiceProductList = invoiceProductService.findAllInvoiceProductsOfCompany();
        model.addAttribute("invoiceProducts", invoiceProductList);

        return "report/stock-report";
    }
    @ExecutionTime
    @GetMapping("/profitLossData")
    public String getProfitLossReport(Model model){
        model.addAttribute("monthlyProfitLossDataMap",invoiceService.getMonthlyProfitLossMap());

        return "/report/profit-loss-report";}
}
