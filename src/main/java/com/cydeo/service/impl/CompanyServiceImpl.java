package com.cydeo.service.impl;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }
    @Override
    public CompanyDto getCompanyDtoByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany();
    }

    @Override
    public CompanyDto findById(Long id) {
        Company company= companyRepository.findById(id).orElseThrow(()->new NoSuchElementException("Company not found"));
        return mapperUtil.convert(company, new CompanyDto());
    }
    @Override
    public List<CompanyDto> listAllCompanies() {

        return companyRepository.findAll().stream()
                .filter(company -> company.getId() != 1)
                .map(company -> mapperUtil.convert(company, new CompanyDto()))
                .sorted(Comparator.comparing((CompanyDto companyDto) -> companyDto.getCompanyStatus().equals(CompanyStatus.PASSIVE))
                        .thenComparing(CompanyDto::getTitle))
                .collect(Collectors.toList());
    }


    @Override
    public List<CompanyDto> listCompaniesByLoggedInUser() {

        if(securityService.getLoggedInUser().getRole().getId()==1L){
            return listAllCompanies();
        }
        if(securityService.getLoggedInUser().getRole().getId()==2L){
            return listAllCompanies().stream().filter(companyDto -> companyDto.getId().equals(securityService.getLoggedInUser().getCompany().getId())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean isTitleExist(CompanyDto companyDto) {
        Company company= companyRepository.findByTitle(companyDto.getTitle()).orElse(null);
        if(company==null){
            return false;
        }
        return !Objects.equals(companyDto.getId(), company.getId());
    }

    @Override
    public CompanyDto saveCompany(CompanyDto companyDto) {
        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        Company company= companyRepository.save(mapperUtil.convert(companyDto, new Company()));
        return mapperUtil.convert(company, new CompanyDto());
    }


    @Override
    public  CompanyDto updateCompany(CompanyDto companyDto) {
        Company company= companyRepository.findById(companyDto.getId()).orElseThrow(()->new CompanyNotFoundException("Company not found"));
        Company updatedCompany= mapperUtil.convert(companyDto, new Company());
        updatedCompany.setId(company.getId());
        updatedCompany.setCompanyStatus(company.getCompanyStatus());
        updatedCompany.setAddress(company.getAddress());
        Company company1=  companyRepository.save(updatedCompany);
        return mapperUtil.convert(company1, new CompanyDto());
    }

    @Override
    public void deactivateCompany(Long id) {
        Company company= companyRepository.findById(id).orElseThrow(()->new CompanyNotFoundException("Company not found"));
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);
    }

    @Override
    public void activateCompany(Long id) {
        Company company = companyRepository.findById(id).orElseThrow(()->new CompanyNotFoundException("Company not found"));
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);
    }
}
