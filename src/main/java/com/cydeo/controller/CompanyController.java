package com.cydeo.controller;
import com.cydeo.dto.CompanyDto;
import com.cydeo.service.AddressService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;



@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final AddressService addressService;
    public CompanyController(CompanyService companyService, AddressService addressService) {
        this.companyService = companyService;
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public String listAllCompanies(Model model) {
        model.addAttribute("companies", companyService.listAllCompanies());
        return "company/company-list";
    }

    @GetMapping("/create")
    public String createCompany(Model model){
        model.addAttribute("newCompany", new CompanyDto());
        model.addAttribute("countries", addressService.getCountryList());
        return "/company/company-create";
    }


    @PostMapping("/create")
    public String insertCompany(@Valid @ModelAttribute("newCompany")CompanyDto companyDto, BindingResult bindingResult, Model model){
        boolean isTitleExist = companyService.isTitleExist(companyDto);
        if (isTitleExist) {
            bindingResult.rejectValue("title", " ", "This title already exists.");

        }
        if(bindingResult.hasErrors()){
            model.addAttribute("countries", addressService.getCountryList());
            return "company/company-create";
        }
        companyService.saveCompany(companyDto);
        return "redirect:/companies/list";
    }


    @GetMapping("/deactivate/{id}")
    public String activateCompany(@PathVariable("id")Long id){
        companyService.deactivateCompany(id);
        return "redirect:/companies/list";
    }


    @GetMapping("/activate/{id}")
    public String deActivateCompany(@PathVariable("id")Long id){
        companyService.activateCompany(id);
        return "redirect:/companies/list";
    }



    @GetMapping("update/{id}")
    public String editCompany(@PathVariable("id")Long id, Model model){
        model.addAttribute("company", companyService.findById(id) );
        model.addAttribute("countries", addressService.getCountryList());
        return "/company/company-update";
    }

    @PostMapping("update/{id}")
    public String updateCompany(@Valid @ModelAttribute("company")CompanyDto companyDto, BindingResult bindingResult, Model model){
        boolean isTitleExist = companyService.isTitleExist(companyDto);
        if (isTitleExist) {
            bindingResult.rejectValue("title", " ", "This title already exists.");
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("countries", addressService.getCountryList());
            return "company/company-update";
        }
        companyService.updateCompany(companyDto);
        return "redirect:/companies/list";
    }














}
