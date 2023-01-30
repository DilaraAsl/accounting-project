package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientVendorDto {
    private Long id;

    @Size(max = 50, min = 2, message = "Company Name should have 2-50 characters long.")
    @NotBlank(message = "Company Name is a required field.")
    private String clientVendorName;

    @NotBlank(message = "Phone Number is a required field.")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" // +111 (202) 555-0125  +1 (202) 555-0125
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"                                  // +111 123 456 789
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "Phone Number is required field and may be in any valid phone number format.")
    private String phone;

   @Pattern(regexp = "^(http:\\/\\/|https:\\/\\/)?(www\\.)?[a-zA-Z0-9-_\\.]+\\.[a-zA-Z]+(:\\d+)?(\\/[a-zA-Z\\d\\.\\-_]*)*[a-zA-Z.!@#$%&=-_'\":,.?\\d*)(]*$", message = "Website should have a valid format.")
    private String website;

    @NotNull(message = "Please select type.")
    private ClientVendorType clientVendorType;

   @Valid
    private AddressDto address;
    private CompanyDto company;

    private List<InvoiceDto> invoices;

}
