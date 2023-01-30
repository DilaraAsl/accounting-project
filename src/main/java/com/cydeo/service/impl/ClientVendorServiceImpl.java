package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.*;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.exception.ClientVendorNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.AddressService;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final MapperUtil mapperUtil;
    private final ClientVendorRepository clientVendorRepository;
    private final SecurityService securityService;
    private final CompanyService companyService;

    private final AddressService addressService;


    public ClientVendorServiceImpl(MapperUtil mapperUtil, ClientVendorRepository clientVendorRepository, SecurityService securityService, CompanyService companyService, AddressService addressService) {
        this.mapperUtil = mapperUtil;
        this.clientVendorRepository = clientVendorRepository;
        this.securityService = securityService;
        this.companyService = companyService;
        this.addressService = addressService;
    }

    public ClientVendorDto findById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id)
                .orElseThrow(() -> new ClientVendorNotFoundException("This client or vendor does not exist "));
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> listAllClientVendors() {
        List<ClientVendor> clientVendorList = clientVendorRepository.findAll();
        return clientVendorList.stream()
                .filter(clientVendor -> clientVendor.getCompany().getTitle().equals(companyService.getCompanyDtoByLoggedInUser().getTitle()))
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .sorted(Comparator.comparing(ClientVendorDto::getClientVendorType).reversed()
                        .thenComparing(ClientVendorDto::getClientVendorName))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> listCompanyVendors() {
        List<ClientVendor> vendorList = clientVendorRepository.findAll()
                .stream()
                .filter(clientOrVendor -> clientOrVendor.getClientVendorType().equals(ClientVendorType.VENDOR))
                .filter(companySelect -> companySelect.getCompany().getId().equals(securityService.getLoggedInUser().getCompany().getId()))
                .collect(Collectors.toList());

        return vendorList.stream().map(vendor -> mapperUtil.convert(vendor, new ClientVendorDto())).collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto save(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = mapperUtil.convert(clientVendorDto, new ClientVendor());
        clientVendor.setCompany(mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company()));
        ClientVendor clientVendor1 = clientVendorRepository.save(clientVendor);
        return mapperUtil.convert(clientVendor1, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> listCompanyClients() {
        List<ClientVendor> clientList = clientVendorRepository.findAll()
                .stream()
                .filter(clientOrVendor -> clientOrVendor.getClientVendorType().equals(ClientVendorType.CLIENT))
                .filter(companySelect -> companySelect.getCompany().getId().equals(companyService.getCompanyDtoByLoggedInUser().getId()))
                .collect(Collectors.toList());
        return clientList.stream().map(vendor -> mapperUtil.convert(vendor, new ClientVendorDto())).collect(Collectors.toList());
    }


    @Override
    public void deleteClientVendorById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id).orElseThrow(() -> new ClientVendorNotFoundException("This client or vendor does not exist "));
        clientVendor.setIsDeleted(true);
        clientVendor.setClientVendorName(clientVendor.getClientVendorName() + "-" + clientVendor.getId());
        clientVendorRepository.save(clientVendor);

    }


    @Override
    public ClientVendorDto update(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = clientVendorRepository.findById(clientVendorDto.getId()).orElseThrow(() -> new ClientVendorNotFoundException("This client or vendor does not exist "));
        ClientVendor updatedClientVendor = mapperUtil.convert(clientVendorDto, new ClientVendor());
        updatedClientVendor.setId(clientVendor.getId());
        updatedClientVendor.setCompany(clientVendor.getCompany());
        updatedClientVendor.getAddress().setId(clientVendor.getAddress().getId());
        ClientVendor clientVendor1 = clientVendorRepository.save(updatedClientVendor);
        return mapperUtil.convert(clientVendor1, new ClientVendorDto());
    }

    @Override
    public boolean isClientVendorExist(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = clientVendorRepository.findByClientVendorName_AndCompany_Title(
                clientVendorDto.getClientVendorName(), companyService.getCompanyDtoByLoggedInUser().getTitle())
                .orElse(null);
        if (clientVendor == null) return false;

        return !Objects.equals(clientVendorDto.getId(), clientVendor.getId());
    }


}






