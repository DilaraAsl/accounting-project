package com.cydeo.service.impl;

import com.cydeo.client.CountryClient;
import com.cydeo.dto.common.CountryListDto;
import com.cydeo.dto.common.TokenDto;
import com.cydeo.entity.Address;
import com.cydeo.repository.AddressRepository;
import com.cydeo.service.AddressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final CountryClient countryClient;

    @Value("${api.country.token}")
    private String token;

    @Value("${api.country.mail}")
    private String email;
    private final AddressRepository addressRepository;


    public AddressServiceImpl(CountryClient countryClient, AddressRepository addressRepository) {
        this.countryClient = countryClient;
        this.addressRepository = addressRepository;
    }

    private String getBearerToken() {
        TokenDto tokenDto = countryClient.auth(email, token);
        return "Bearer " + tokenDto.getAuthToken();
    }

    @Override
    public List<String> getCountryList() {
        List<CountryListDto> countries = countryClient.getCountry(getBearerToken());
        return countries.stream()
                .map(CountryListDto::getCountryName)
                .collect(Collectors.toList());
    }

}
