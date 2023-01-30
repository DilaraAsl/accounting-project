package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;

import java.util.List;

public interface CompanyService {

    CompanyDto getCompanyDtoByLoggedInUser();

    CompanyDto findById(Long id);

    List<CompanyDto> listAllCompanies();

    CompanyDto saveCompany(CompanyDto companyDto);

    CompanyDto updateCompany(CompanyDto companyDto);

    void deactivateCompany(Long id);

    void activateCompany(Long id);
    List<CompanyDto> listCompaniesByLoggedInUser();

    boolean isTitleExist(CompanyDto companyDto);
}
