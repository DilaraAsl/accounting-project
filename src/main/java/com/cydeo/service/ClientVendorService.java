package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Address;
import com.cydeo.enums.ClientVendorType;

import java.net.DatagramPacket;
import java.util.List;

public interface ClientVendorService {

    ClientVendorDto findById(Long id);
    
    List<ClientVendorDto> listCompanyVendors();

    List<ClientVendorDto> listAllClientVendors();

    ClientVendorDto save(ClientVendorDto  clientVendorDto);
    List<ClientVendorDto> listCompanyClients();

    void deleteClientVendorById(Long  id);

    ClientVendorDto update(ClientVendorDto  clientVendorDto);

    boolean isClientVendorExist(ClientVendorDto clientVendorDto);


}
