package com.cydeo.controller;

import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final InvoiceService invoiceService;

    public DashboardController(DashboardService dashboardService, InvoiceService invoiceService) {
        this.dashboardService = dashboardService;
        this.invoiceService = invoiceService;
    }

    // this method has only dummy info and should be modified in accordance with user stories.
    @GetMapping("/dashboard")
    public String dashboard(Model model){
        Map<String, BigDecimal> summaryNumbers = Map.of(
                "totalCost", invoiceService.countTotalPurchase(),
                "totalSales", invoiceService.countTotalSales(),
                "profitLoss", invoiceService.profitOrLoss()
        );
        model.addAttribute("summaryNumbers", summaryNumbers);
        model.addAttribute("invoices",invoiceService.findLastThreeApprovedInvoice());
        model.addAttribute("exchangeRates",dashboardService.listUsdExchangeRate());
        model.addAttribute("title", "Cydeo Accounting-Dashboard");
        return "dashboard";
    }

}
