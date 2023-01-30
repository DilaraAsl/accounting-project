package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.engine.internal.Cascade;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "clients_vendors")
@Where(clause = "is_deleted='false'")
public class ClientVendor extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ClientVendorType clientVendorType;
    @Column(unique = true)
    private String clientVendorName;
    private String phone;
    private String website;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "clientVendor")
    private List<Invoice> invoices;

    public ClientVendor(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, Boolean isDeleted, ClientVendorType clientVendorType, String clientVendorName, String phone, String website, Address address, Company company) {
        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId, isDeleted);
        this.clientVendorType = clientVendorType;
        this.clientVendorName = clientVendorName;
        this.phone = phone;
        this.website = website;
        this.address = address;
        this.company = company;
    }
}
