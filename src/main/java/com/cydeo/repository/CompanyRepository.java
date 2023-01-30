package com.cydeo.repository;

import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByTitle(String title);

   boolean existsByTitle(String companyTitle);




}
