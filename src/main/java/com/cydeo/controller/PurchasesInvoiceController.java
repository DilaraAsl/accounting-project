package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {
    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final InvoiceProductService invoiceProductService;
    private final CompanyService companyService;

    public PurchasesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService, ProductService productService, InvoiceProductService invoiceProductService, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.invoiceProductService = invoiceProductService;
        this.companyService = companyService;
    }


    @GetMapping("/list")
    String listPurchaseInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listSalesOrPurchaseInvoices(InvoiceType.PURCHASE));

        return "/invoice/purchase-invoice-list";

    }

    @GetMapping("/update/{invoice}")
    public String editPurchasesInvoice(@PathVariable("invoice") Long id, Model model) {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));


        return "/invoice/purchase-invoice-update";

    }

    @PostMapping("/update/{id}")
    public String saveUpdateIntoPurchaseList(@Valid @ModelAttribute InvoiceDto invoiceDto, BindingResult bindingResult
            ,@PathVariable("id") Long id) {
        if (bindingResult.hasErrors()){
            return "/invoice/purchase-invoice-update";
        }
        invoiceService.update(invoiceDto);
        return "redirect:/purchaseInvoices/update/" + id;


    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addProductIntoProductList(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, @PathVariable("invoiceId") Long id, Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(id));
            model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));

            return "/invoice/purchase-invoice-update";
        }

        invoiceProductService.add(invoiceProductDto, id);
        return "redirect:/purchaseInvoices/update/" + id;

    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String deleteProductFromProductList(@PathVariable("invoiceId") Long id, @PathVariable("invoiceProductId") Long id2) {
        invoiceProductService.delete(id2);
        return "redirect:/purchaseInvoices/update/" + id;
    }

    @GetMapping("/create")
    public String createPurchaseInvoice(Model model) {

        model.addAttribute("newPurchaseInvoice", invoiceService.generateNewInvoiceDto(InvoiceType.PURCHASE));
        return "/invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String savePurchaseInvoice(@Valid @ModelAttribute("newPurchaseInvoice") InvoiceDto invoiceDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/invoice/purchase-invoice-create";
        }
        InvoiceDto newInvoiceDto = invoiceService.saveInvoice(invoiceDto, InvoiceType.PURCHASE);
        return "redirect:/purchaseInvoices/update/" + newInvoiceDto.getId();
    }

    @GetMapping("/approve/{id}")
    public String approvePurchaseInvoice(@PathVariable("id") Long id) {
        invoiceService.approve(id);
        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("/delete/{id}")// when delete button is check html->this path is activated
    public String deletePurchaseInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);

        return "redirect:/purchaseInvoices/list";
    }


    @GetMapping("/print/{id}")
    public String printPurchasesInvoice(@PathVariable("id") Long id, Model model) {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("company", companyService.getCompanyDtoByLoggedInUser());
        model.addAttribute("invoiceProducts", invoiceProductService.findTotalPriceWithAndWithoutTax(id));

        return "invoice/invoice_print";
    }

    @ModelAttribute
    private void commonAttributes(Model model) {
        model.addAttribute("vendors", clientVendorService.listCompanyVendors());
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("title", "Cydeo Accounting-Purchase Invoice");

    }

}
