package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ClientVendorRepository extends JpaRepository<ClientVendor, Long> {

    List<ClientVendor> findAllByClientVendorType(String clientVendorType );
    Optional<ClientVendor> findByClientVendorName_AndCompany_Title(String clientVendorName, String companyTitle);

}
