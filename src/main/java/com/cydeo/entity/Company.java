
package com.cydeo.entity;
import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.CompanyStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(unique=true)
    private String title;

    private String phone;

    private String website;

    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Address address;

    public Company(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, Boolean isDeleted, String title, String phone, String website, CompanyStatus companyStatus, Address address) {
        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId, isDeleted);
        this.title = title;
        this.phone = phone;
        this.website = website;
        this.companyStatus = companyStatus;
        this.address = address;
    }
}
